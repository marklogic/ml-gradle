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
package com.marklogic.mgmt;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.RestConfig;
import com.marklogic.rest.util.RestTemplateUtil;
import org.jdom2.Namespace;
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

/**
 * Wraps a RestTemplate with methods that should simplify accessing the Manage API with RestTemplate. Each NounManager
 * should depend on an instance of ManageClient for accessing the Manage API.
 */
public class ManageClient extends LoggingObject {

	private ManageConfig manageConfig;
	private RestTemplate restTemplate;
	private RestTemplate securityUserRestTemplate;
	private PayloadParser payloadParser;

	/**
	 * Creates an uninitialized instance that requires a {@code ManageConfig} to be provided in order to be operable.
	 *
	 * @deprecated since 4.5.0; will be removed in 5.0.0
	 */
    public ManageClient() {
    }

    public ManageClient(ManageConfig config) {
        setManageConfig(config);
    }

	/**
	 * Uses the given ManageConfig instance to construct a Spring RestTemplate for communicating with the Manage API.
	 * In addition, if adminUsername on the ManageConfig instance differs from username, then a separate RestTemplate is
	 * constructed for making calls to the Manage API that need user with the manage-admin and security roles, which is
	 * often an admin user.
	 *
	 * @param config
	 */
	public void setManageConfig(ManageConfig config) {
	    this.manageConfig = config;
	    if (logger.isInfoEnabled()) {
		    logger.info("Initializing ManageClient with manage config of: " + config);
	    }
    }

	/**
	 * Use this when you want to provide your own RestTemplate as opposed to using the one that's constructed via a
	 * ManageConfig instance.
	 *
	 * @param restTemplate
	 */
	public ManageClient(RestTemplate restTemplate) {
    	this(restTemplate, restTemplate);
    }

	/**
	 * Use this when you want to provide your own RestTemplate as opposed to using the one that's constructed via a
	 * ManageConfig instance.
	 *
	 * @param restTemplate
	 * @param adminRestTemplate
	 */
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
        return getRestTemplate().getForObject(buildUri(path), String.class);
    }

    public Fragment getXml(String path, String... namespacePrefixesAndUris) {
        String xml = getXmlString(path);
        List<Namespace> list = new ArrayList<>();
        for (int i = 0; i < namespacePrefixesAndUris.length; i += 2) {
            list.add(Namespace.getNamespace(namespacePrefixesAndUris[i], namespacePrefixesAndUris[i + 1]));
        }
        return new Fragment(xml, list.toArray(new Namespace[] {}));
    }

	public String getXmlStringAsSecurityUser(String path) {
		logSecurityUserRequest(path, "XML", "GET");
		return getSecurityUserRestTemplate().getForObject(buildUri(path), String.class);
	}

	public Fragment getXmlAsSecurityUser(String path, String... namespacePrefixesAndUris) {
		String xml = getXmlStringAsSecurityUser(path);
		List<Namespace> list = new ArrayList<>();
		for (int i = 0; i < namespacePrefixesAndUris.length; i += 2) {
			list.add(Namespace.getNamespace(namespacePrefixesAndUris[i], namespacePrefixesAndUris[i + 1]));
		}
		return new Fragment(xml, list.toArray(new Namespace[] {}));
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

	public void delete(String path) {
        logRequest(path, "", "DELETE");
        getRestTemplate().delete(buildUri(path));
    }

    public void deleteAsSecurityUser(String path) {
	    logSecurityUserRequest(path, "", "DELETE");
	    getSecurityUserRestTemplate().delete(buildUri(path));
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
        	String username = manageConfig != null ?
				String.format("as user '%s' ", manageConfig.getUsername()) : "";
            logger.info("Sending {} {} request {}to path: {}", contentType, method, username, buildUri(path));
        }
    }

    protected void logSecurityUserRequest(String path, String contentType, String method) {
        if (logger.isInfoEnabled()) {
			String username = determineUsernameForSecurityUserRequest();
			if (!"".equals(username)) {
				username = String.format("as user '%s' (who should have the 'manage-admin' and 'security' roles) ", username);
			}
            logger.info("Sending {} {} request {}to path: {}", contentType, method, username, buildUri(path));
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

	public URI buildUri(String path) {
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

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RestTemplate getSecurityUserRestTemplate() {
		if (this.securityUserRestTemplate == null) {
			initializeSecurityUserRestTemplate();
		}
		return securityUserRestTemplate;
	}

	public void setSecurityUserRestTemplate(RestTemplate restTemplate) {
		this.securityUserRestTemplate = restTemplate;
	}
}
