/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

/**
 * Spring RestTemplate interceptor that prevents any PUT or POST request from completing and instead captures the
 * change that will result from the request.
 * <p>
 * Also extends Spring's DefaultResponseErrorHandler class so that certain errors aren't treated as errors during a
 * preview.
 */
public class PreviewInterceptor extends DefaultResponseErrorHandler implements ClientHttpRequestInterceptor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ManageClient manageClient;

	private ArrayNode results;

	public PreviewInterceptor(ManageClient manageClient) {
		this.manageClient = manageClient;
		this.results = ObjectMapperFactory.getObjectMapper().createArrayNode();
	}

	/**
	 * A 404 can happen during a preview when deploying database-specific resources like CPF pipelines or domains.
	 * If the associated triggers database hasn't been created yet, a 404 will occur. But that shouldn't interrupt a
	 * deployment process. So a 404 is logged but not treated as an error.
	 *
	 * @param statusCode
	 * @return
	 */
	@Override
	protected boolean hasError(HttpStatus statusCode) {
		if (HttpStatus.NOT_FOUND.equals(statusCode)) {
			logger.info("Received a 404 response, but ignoring while doing a preview");
			return false;
		}
		return super.hasError(statusCode);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
		if (HttpMethod.PUT.equals(request.getMethod())) {
			return previewPut(request, bytes);
		} else if (HttpMethod.POST.equals(request.getMethod())) {
			return previewPost(request, bytes);
		} else if (HttpMethod.DELETE.equals(request.getMethod())) {
			return previewDelete(request, bytes);
		}
		return execution.execute(request, bytes);
	}

	/**
	 * For a PUT request, need to get the existing resource as JSON; merge the incoming payload, represented by the byte
	 * array, into that JSON; and then compare the results to the existing JSON to produce a diff as a JSON patch. The
	 * results of this are then added to the report.
	 *
	 * @param request
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	protected ClientHttpResponse previewPut(HttpRequest request, byte[] bytes) throws IOException {
		logger.info("Previewing PUT to: " + request.getURI());

		String payload = new String(bytes).trim();

		if (new PayloadParser().isJsonPayload(payload)) {
			ObjectNode existingResource = getExistingResource(request);
			JsonNode diff = buildJsonPatch(existingResource, payload);
			includeJsonPatchInReport(request, existingResource, diff);
		} else {
			logger.info("Payload is XML, preview is not yet supported for XML");

			ObjectNode result = ObjectMapperFactory.getObjectMapper().createObjectNode();
			result.set("message", new TextNode("Preview not supported for XML resource files, so not performing preview for PUT to: " + request.getURI()));
			results.add(result);
		}

		logger.info("Previewing, so not sending PUT to: " + request.getURI());

		return newFakeResponse();
	}

	/**
	 * Uses zjsonpatch to capture the difference between the incoming request and the existing resource as a JSON patch.
	 *
	 * @param existingResource
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	protected JsonNode buildJsonPatch(ObjectNode existingResource, String payload) throws IOException {
		ObjectNode payloadResource = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(payload);
		modifyPayloadBeforePreview(payloadResource);

		ObjectNode merged = JsonNodeUtil.mergeObjectNodes(existingResource, payloadResource);
		EnumSet<DiffFlags> flags = EnumSet.of(DiffFlags.OMIT_VALUE_ON_REMOVE, DiffFlags.OMIT_MOVE_OPERATION, DiffFlags.OMIT_COPY_OPERATION, DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE);
		return JsonDiff.asJson(existingResource, merged, flags);
	}

	protected void includeJsonPatchInReport(HttpRequest request, ObjectNode existingResource, JsonNode jsonPatch) {
		ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
		if (jsonPatch instanceof ArrayNode && jsonPatch.size() > 0) {
			ObjectNode result = mapper.createObjectNode();
			result.set("message", new TextNode("Will update resource at: " + request.getURI()));
			result.set("existingResource", existingResource);
			result.set("patch", jsonPatch);
			results.add(result);
		} else {
			ObjectNode result = mapper.createObjectNode();
			result.set("message", new TextNode("No changes for resource at: " + request.getURI()));
			results.add(result);
		}
	}

	/**
	 * A PUT request is made to a "properties" endpoint for a resource that can also be used for getting the JSON for
	 * the existing resource.
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	protected ObjectNode getExistingResource(HttpRequest request) throws IOException {
		String existingJson = manageClient.getJson(request.getURI());
		return (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(existingJson);
	}

	/**
	 * Some resources need to be modified to account for difference that will always exist with an incoming request,
	 * as that request may have properties that won't ever exist in the resource JSON retrieved from the Manage API,
	 * such as a user's password.
	 *
	 * @param payload
	 */
	protected void modifyPayloadBeforePreview(ObjectNode payload) {
		// Passwords will never be present in the source, so remove from the target
		if (payload.has("password")) {
			payload.remove("password");
		}

		// If the payload is for a view-schema, schema-name will never be present
		if (payload.has("view-name") && payload.has("schema-name")) {
			payload.remove("schema-name");
		}
	}

	/**
	 * For a POST operation, a new resource is being created, so just need to include the resource in the report - no
	 * diff is needed.
	 *
	 * @param request
	 * @param bytes
	 * @return
	 */
	protected ClientHttpResponse previewPost(HttpRequest request, byte[] bytes) throws IOException {
		ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
		ObjectNode result = mapper.createObjectNode();

		if (request.getURI() != null && request.getURI().toString().endsWith("/manage/v3")) {
			String message = "Previewing POST calls to /manage/v3 are not yet supported";
			result.set("message", new TextNode(message));
			results.add(result);
			logger.info(message);
			return newFakeResponse();
		}

		result.set("message", new TextNode("Will create new resource at: " + request.getURI()));

		String payload = new String(bytes).trim();
		if (new PayloadParser().isJsonPayload(payload)) {
			ObjectNode node = (ObjectNode)mapper.readTree(payload);
			if (node.has("password")) {
				node.remove("password");
			}
			result.set("resource", node);
		} else {
			result.set("resource", new TextNode(payload));
		}

		results.add(result);

		logger.info("Previewing, so not sending POST to: " + request.getURI());

		return newFakeResponse();
	}

	/**
	 * For a DELETE operation, a resource is being deleted, so just need to include the resource URI in the report - no
	 * diff is needed.
	 *
	 * @param request
	 * @param bytes
	 * @return
	 */
	protected ClientHttpResponse previewDelete(HttpRequest request, byte[] bytes) throws IOException {
		logger.info("Previewing, so not sending DELETE to: " + request.getURI());
		return newFakeResponse();
	}

	/**
	 * Creates a "fake" response for PUT and POST requests. Testing so far shows that simply returning a 200/OK
	 * suffices during a preview.
	 *
	 * @return
	 */
	protected ClientHttpResponse newFakeResponse() {
		return new ClientHttpResponse() {
			@Override
			public HttpStatus getStatusCode() {
				return HttpStatus.OK;
			}

			@Override
			public int getRawStatusCode() {
				return 200;
			}

			@Override
			public String getStatusText() {
				return null;
			}

			@Override
			public void close() {
			}

			@Override
			public InputStream getBody() {
				// Need to return something besides null, which can cause null pointer exceptions
				return new ByteArrayInputStream(new byte[]{});
			}

			@Override
			public HttpHeaders getHeaders() {
				return new HttpHeaders();
			}
		};
	}

	public ArrayNode getResults() {
		return results;
	}
}
