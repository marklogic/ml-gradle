/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.RestConfig;
import com.marklogic.rest.util.RestTemplateUtil;
import org.jdom2.Namespace;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wraps a RestTemplate with methods that should simplify accessing the Manage API with RestTemplate. Each NounManager
 * should depend on an instance of ManageClient for accessing the Manage API.
 */
public class ManageClient extends LoggingObject {

	private ManageConfig manageConfig;
	private RestTemplate restTemplate;
	private RestTemplate securityUserRestTemplate;
	private PayloadParser payloadParser;

	public ManageClient(ManageConfig config) {
		this.manageConfig = config;
		if (logger.isInfoEnabled()) {
			logger.info("Initializing ManageClient with manage config of: {}", config);
		}
	}

	/**
	 * Deprecated in 6.0.1 with the intention of removing in 7.0.0 so that the underlying ManageConfig can be declared
	 * as final.
	 *
	 * @deprecated
	 */
	@Deprecated(since = "6.0.1", forRemoval = true)
	public void setManageConfig(ManageConfig config) {
		this.manageConfig = config;
	}

	/**
	 * Deprecated in 6.0.1 as it will not work without a ManageConfig instance being set, which is then unlikely to
	 * be consistent with the given RestTemplate.
	 * @deprecated
	 */
	@Deprecated(since = "6.0.1", forRemoval = true)
	public ManageClient(RestTemplate restTemplate) {
		this(restTemplate, restTemplate);
	}

	/**
	 * Deprecated in 6.0.1 as it will not work without a ManageConfig instance being set, which is then unlikely to
	 * be consistent with the given RestTemplate.
	 * @deprecated
	 */
	@Deprecated(since = "6.0.1", forRemoval = true)
	public ManageClient(RestTemplate restTemplate, RestTemplate adminRestTemplate) {
		this.restTemplate = restTemplate;
		this.securityUserRestTemplate = adminRestTemplate;
	}

	public ResponseEntity<String> putJson(String path, String json) {
		logRequest(path, "JSON", "PUT");
		return getRestTemplate().exchange(buildUri(path), HttpMethod.PUT, buildJsonEntity(json), String.class);
	}

	public ResponseEntity<String> putJsonAsSecurityUser(String path, String json) {
		logSecurityUserRequest(path, "JSON", "PUT");
		return getSecurityUserRestTemplate().exchange(buildUri(path), HttpMethod.PUT, buildJsonEntity(json), String.class);
	}

	public ResponseEntity<String> putXml(String path, String xml) {
		logRequest(path, "XML", "PUT");
		return getRestTemplate().exchange(buildUri(path), HttpMethod.PUT, buildXmlEntity(xml), String.class);
	}

	public ResponseEntity<String> putXmlAsSecurityUser(String path, String xml) {
		logSecurityUserRequest(path, "XML", "PUT");
		return getSecurityUserRestTemplate().exchange(buildUri(path), HttpMethod.PUT, buildXmlEntity(xml), String.class);
	}

	public ResponseEntity<String> postJson(String path, String json) {
		logRequest(path, "JSON", "POST");
		return getRestTemplate().exchange(buildUri(path), HttpMethod.POST, buildJsonEntity(json), String.class);
	}

	public ResponseEntity<String> postJsonAsSecurityUser(String path, String json) {
		logSecurityUserRequest(path, "JSON", "POST");
		return getSecurityUserRestTemplate().exchange(buildUri(path), HttpMethod.POST, buildJsonEntity(json), String.class);
	}

	public ResponseEntity<String> postXml(String path, String xml) {
		logRequest(path, "XML", "POST");
		return getRestTemplate().exchange(buildUri(path), HttpMethod.POST, buildXmlEntity(xml), String.class);
	}

	public ResponseEntity<String> postXmlAsSecurityUser(String path, String xml) {
		logSecurityUserRequest(path, "XML", "POST");
		return getSecurityUserRestTemplate().exchange(buildUri(path), HttpMethod.POST, buildXmlEntity(xml), String.class);
	}

	public ResponseEntity<String> postForm(String path, String... params) {
		logRequest(path, "form", "POST");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		for (int i = 0; i < params.length; i += 2) {
			map.add(params[i], params[i + 1]);
		}
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
		return getRestTemplate().exchange(buildUri(path), HttpMethod.POST, entity, String.class);
	}

	public String getXmlString(String path) {
		logRequest(path, "XML", "GET");
		// coverity [Improper Control of Resource Identifiers ('Resource Injection')]
		return getRestTemplate().getForObject(buildUri(path), String.class);
	}

	public Fragment getXml(String path, String... namespacePrefixesAndUris) {
		String xml = getXmlString(path);
		Objects.requireNonNull(xml);
		List<Namespace> list = new ArrayList<>();
		for (int i = 0; i < namespacePrefixesAndUris.length; i += 2) {
			list.add(Namespace.getNamespace(namespacePrefixesAndUris[i], namespacePrefixesAndUris[i + 1]));
		}
		return new Fragment(xml, list.toArray(new Namespace[]{}));
	}

	public String getXmlStringAsSecurityUser(String path) {
		logSecurityUserRequest(path, "XML", "GET");
		return getSecurityUserRestTemplate().getForObject(buildUri(path), String.class);
	}

	public Fragment getXmlAsSecurityUser(String path, String... namespacePrefixesAndUris) {
		String xml = getXmlStringAsSecurityUser(path);
		Objects.requireNonNull(xml);
		List<Namespace> list = new ArrayList<>();
		for (int i = 0; i < namespacePrefixesAndUris.length; i += 2) {
			list.add(Namespace.getNamespace(namespacePrefixesAndUris[i], namespacePrefixesAndUris[i + 1]));
		}
		return new Fragment(xml, list.toArray(new Namespace[]{}));
	}

	public String getJson(String path) {
		return getJson(path, String.class).getBody();
	}

	public JsonNode getJsonNode(String path) {
		return getJson(path, JsonNode.class).getBody();
	}

	protected <T> ResponseEntity<T> getJson(String path, Class<T> responseType) {
		logRequest(path, "JSON", "GET");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return getRestTemplate().exchange(buildUri(path), HttpMethod.GET, new HttpEntity<>(headers), responseType);
	}

	public String getJson(URI uri) {
		logRequest(uri.toString(), "JSON", "GET");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return getRestTemplate().exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
	}

	public String getJsonAsSecurityUser(String path) {
		logSecurityUserRequest(path, "JSON", "GET");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return getSecurityUserRestTemplate().exchange(buildUri(path), HttpMethod.GET, new HttpEntity<>(headers), String.class)
			.getBody();
	}

	public void delete(String path, String... headerNamesAndValues) {
		logRequest(path, "", "DELETE");
		delete(getRestTemplate(), path, headerNamesAndValues);
	}

	public void deleteAsSecurityUser(String path, String... headerNamesAndValues) {
		logSecurityUserRequest(path, "", "DELETE");
		delete(getSecurityUserRestTemplate(), path, headerNamesAndValues);
	}

	private void delete(RestTemplate restTemplate, String path, String... headerNamesAndValues) {
		URI uri = buildUri(path);
		HttpHeaders headers = new HttpHeaders();
		if (headerNamesAndValues != null) {
			for (int i = 0; i < headerNamesAndValues.length; i += 2) {
				headers.add(headerNamesAndValues[i], headerNamesAndValues[i + 1]);
			}
		}
		HttpEntity<Resource> entity = new HttpEntity<>(null, headers);
		restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);
	}

	/**
	 * Per #187 and version 3.1.0, when an HttpEntity is constructed with a JSON payload, this method will check to see
	 * if it should "clean" the JSON via the Jackson library, which is primarily intended for removing comments from
	 * JSON (comments that Jackson allows, but aren't allowed by the JSON spec). This behavior is disabled by default.
	 *
	 * @param json
	 * @return
	 */
	public HttpEntity<String> buildJsonEntity(String json) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		if (manageConfig != null && manageConfig.isCleanJsonPayloads()) {
			json = cleanJsonPayload(json);
		}
		return new HttpEntity<>(json, headers);
	}

	/**
	 * Per #187, and version 3.1.0, this will also use Jackson to remove any comments in the JSON payload, as Jackson
	 * is now configured to ignore comments, but we still don't want to include them in the payload sent to MarkLogic.
	 *
	 * @param payload
	 * @return
	 */
	protected String cleanJsonPayload(String payload) {
		if (payloadParser == null) {
			payloadParser = new PayloadParser();
		}
		JsonNode node = payloadParser.parseJson(payload);
		StringWriter sw = new StringWriter();
		try {
			ObjectMapperFactory.getObjectMapper().writer().writeValue(sw, node);
		} catch (IOException ex) {
			throw new RuntimeException("Unable to write JSON payload as JsonNode back out to a string, cause: " + ex.getMessage());
		}
		return sw.toString();
	}

	public HttpEntity<String> buildXmlEntity(String xml) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		return new HttpEntity<>(xml, headers);
	}

	protected void logRequest(String path, String contentType, String method) {
		if (logger.isInfoEnabled()) {
			String username = manageConfig != null && StringUtils.hasText(manageConfig.getUsername()) ?
				String.format("as user '%s' ", manageConfig.getUsername()) : "";
			URI uri = buildUri(path);
			logger.info("Sending {} {} request {}to path: {}", contentType, method, username, uri);
		}
	}

	protected void logSecurityUserRequest(String path, String contentType, String method) {
		if (logger.isInfoEnabled()) {
			String username = determineUsernameForSecurityUserRequest();
			if (!"".equals(username)) {
				username = String.format("as user '%s' (who should have the 'manage-admin' and 'security' roles) ", username);
			}
			URI uri = buildUri(path);
			logger.info("Sending {} {} request {}to path: {}", contentType, method, username, uri);
		}
	}

	protected String determineUsernameForSecurityUserRequest() {
		String username = "";
		if (manageConfig != null) {
			username = manageConfig.getSecurityUsername();
			if (!StringUtils.hasText(username)) {
				username = manageConfig.getUsername();
			}
		}
		return username;
	}

	private void initializeSecurityUserRestTemplate() {
		String securityUsername = this.manageConfig.getSecurityUsername();
		if (securityUsername != null && securityUsername.trim().length() > 0 && !securityUsername.equals(this.manageConfig.getUsername())) {
			if (logger.isInfoEnabled()) {
				logger.info(format("Initializing separate connection to Manage API with user '%s' that should have the 'manage-admin' and 'security' roles", securityUsername));
			}

			RestConfig rc = new RestConfig(this.manageConfig);
			// Override settings based on the 3 "security user"-specific properties known by ManageConfig.
			// Note that in 4.5.0, with the addition of cloud/certificate/kerberos/saml auth, this will only have any
			// impact if the user is using digest or basic auth. There's no equivalent of a separate "security" user
			// yet for the other 4 authentication types.
			rc.setUsername(this.manageConfig.getSecurityUsername());
			rc.setPassword(this.manageConfig.getSecurityPassword());
			if (this.manageConfig.getSecuritySslContext() != null) {
				rc.setSslContext(this.manageConfig.getSecuritySslContext());
			}
			this.securityUserRestTemplate = RestTemplateUtil.newRestTemplate(rc);
		} else {
			this.securityUserRestTemplate = getRestTemplate();
		}
	}

	/**
	 * Builds a secure URI from the given path by delegating to RestConfig.buildUri().
	 * This method prevents URL manipulation attacks by using Spring's UriComponentsBuilder
	 * to properly encode and validate all path components and query parameters.
	 *
	 * @param path The path to build a URI for - this input is sanitized and validated
	 * @return A secure URI that prevents injection attacks
	 */
	public URI buildUri(String path) {
		Objects.requireNonNull(manageConfig, "A ManageConfig instance must be provided");
		// Delegate to RestConfig.buildUri() which uses Spring's UriComponentsBuilder
		// to safely construct URIs and prevent URL manipulation attacks
		return manageConfig.buildUri(path);
	}

	public RestTemplate getRestTemplate() {
		if (this.restTemplate == null) {
			this.restTemplate = RestTemplateUtil.newRestTemplate(this.manageConfig);
		}
		return restTemplate;
	}

	public ManageConfig getManageConfig() {
		return manageConfig;
	}

	@Deprecated(since = "6.0.1", forRemoval = true)
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RestTemplate getSecurityUserRestTemplate() {
		if (this.securityUserRestTemplate == null) {
			initializeSecurityUserRestTemplate();
		}
		return securityUserRestTemplate;
	}

	@Deprecated(since = "6.0.1", forRemoval = true)
	public void setSecurityUserRestTemplate(RestTemplate restTemplate) {
		this.securityUserRestTemplate = restTemplate;
	}
}
