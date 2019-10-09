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
     * Can use this constructor when the default values in ManageConfig will work.
     */
    public ManageClient() {
        this(new ManageConfig());
    }

    public ManageClient(ManageConfig config) {
        setManageConfig(config);
    }

	/**
	 * Use setManageConfig instead.
	 *
	 * @param config
	 */
	@Deprecated
	public void initialize(ManageConfig config) {
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
	    this.restTemplate = RestTemplateUtil.newRestTemplate(config);

	    String securityUsername = config.getSecurityUsername();
	    if (securityUsername != null && securityUsername.trim().length() > 0 && !securityUsername.equals(config.getUsername())) {
		    if (logger.isInfoEnabled()) {
			    logger.info(format("Initializing separate connection to Manage API with user '%s' that must have both manage-admin and security roles", securityUsername));
		    }

		    RestConfig rc = new RestConfig(config.getHost(), config.getPort(), securityUsername, config.getSecurityPassword());
		    rc.setScheme(config.getScheme());
		    rc.setConfigureSimpleSsl(config.isConfigureSimpleSsl());
		    rc.setHostnameVerifier(config.getHostnameVerifier());

		    if (config.getSecuritySslContext() != null) {
		    	rc.setSslContext(config.getSecuritySslContext());
		    } else {
		    	rc.setSslContext(config.getSslContext());
		    }

		    this.securityUserRestTemplate = RestTemplateUtil.newRestTemplate(rc);
	    } else {
		    this.securityUserRestTemplate = restTemplate;
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
        return restTemplate.exchange(buildUri(path), HttpMethod.PUT, buildJsonEntity(json), String.class);
    }

	/**
	 * Use putJsonAsSecurityUser instead.
	 *
	 * @param path
	 * @param json
	 * @return
	 */
	@Deprecated
    public ResponseEntity<String> putJsonAsAdmin(String path, String json) {
		return putJsonAsSecurityUser(path, json);
    }

	public ResponseEntity<String> putJsonAsSecurityUser(String path, String json) {
		logSecurityUserRequest(path, "JSON", "PUT");
		return securityUserRestTemplate.exchange(buildUri(path), HttpMethod.PUT, buildJsonEntity(json), String.class);
	}

    public ResponseEntity<String> putXml(String path, String xml) {
        logRequest(path, "XML", "PUT");
        return restTemplate.exchange(buildUri(path), HttpMethod.PUT, buildXmlEntity(xml), String.class);
    }

	/**
	 * Use putXmlAsSecurityUser.
	 *
	 * @param path
	 * @param xml
	 * @return
	 */
	@Deprecated
    public ResponseEntity<String> putXmlAsAdmin(String path, String xml) {
		return putXmlAsSecurityUser(path, xml);
    }

	public ResponseEntity<String> putXmlAsSecurityUser(String path, String xml) {
		logSecurityUserRequest(path, "XML", "PUT");
		return securityUserRestTemplate.exchange(buildUri(path), HttpMethod.PUT, buildXmlEntity(xml), String.class);
	}

    public ResponseEntity<String> postJson(String path, String json) {
        logRequest(path, "JSON", "POST");
        return restTemplate.exchange(buildUri(path), HttpMethod.POST, buildJsonEntity(json), String.class);
    }

	/**
	 * Use postJsonAsSecurityUser instead.
	 *
	 * @param path
	 * @param json
	 * @return
	 */
	@Deprecated
    public ResponseEntity<String> postJsonAsAdmin(String path, String json) {
		return postJsonAsSecurityUser(path, json);
    }

	public ResponseEntity<String> postJsonAsSecurityUser(String path, String json) {
		logSecurityUserRequest(path, "JSON", "POST");
		return securityUserRestTemplate.exchange(buildUri(path), HttpMethod.POST, buildJsonEntity(json), String.class);
	}

    public ResponseEntity<String> postXml(String path, String xml) {
        logRequest(path, "XML", "POST");
        return restTemplate.exchange(buildUri(path), HttpMethod.POST, buildXmlEntity(xml), String.class);
    }

	/**
	 * Use postXmlAsSecurityUser instead.
	 *
	 * @param path
	 * @param xml
	 * @return
	 */
	@Deprecated
    public ResponseEntity<String> postXmlAsAdmin(String path, String xml) {
		return postXmlAsSecurityUser(path, xml);
    }

	public ResponseEntity<String> postXmlAsSecurityUser(String path, String xml) {
		logSecurityUserRequest(path, "XML", "POST");
		return securityUserRestTemplate.exchange(buildUri(path), HttpMethod.POST, buildXmlEntity(xml), String.class);
	}

	public ResponseEntity<String> postForm(String path, String... params) {
        logRequest(path, "form", "POST");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        for (int i = 0; i < params.length; i += 2) {
            map.add(params[i], params[i + 1]);
        }
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.exchange(buildUri(path), HttpMethod.POST, entity, String.class);
    }

    public String getXmlString(String path) {
        logRequest(path, "XML", "GET");
        return getRestTemplate().getForObject(buildUri(path), String.class);
    }

    public Fragment getXml(String path, String... namespacePrefixesAndUris) {
        String xml = getXmlString(path);
        List<Namespace> list = new ArrayList<Namespace>();
        for (int i = 0; i < namespacePrefixesAndUris.length; i += 2) {
            list.add(Namespace.getNamespace(namespacePrefixesAndUris[i], namespacePrefixesAndUris[i + 1]));
        }
        return new Fragment(xml, list.toArray(new Namespace[] {}));
    }

	/**
	 * Use getXmlStringAsSecurityUser instead.
	 *
	 * @param path
	 * @return
	 */
	@Deprecated
	public String getXmlStringAsAdmin(String path) {
		return getXmlStringAsSecurityUser(path);
	}

	public String getXmlStringAsSecurityUser(String path) {
		logSecurityUserRequest(path, "XML", "GET");
		return securityUserRestTemplate.getForObject(buildUri(path), String.class);
	}

	/**
	 * Use getXmlAsSecurityUser instead.
	 *
	 * @param path
	 * @param namespacePrefixesAndUris
	 * @return
	 */
	@Deprecated
    public Fragment getXmlAsAdmin(String path, String... namespacePrefixesAndUris) {
		return getXmlAsSecurityUser(path, namespacePrefixesAndUris);
    }

	public Fragment getXmlAsSecurityUser(String path, String... namespacePrefixesAndUris) {
		String xml = getXmlStringAsSecurityUser(path);
		List<Namespace> list = new ArrayList<Namespace>();
		for (int i = 0; i < namespacePrefixesAndUris.length; i += 2) {
			list.add(Namespace.getNamespace(namespacePrefixesAndUris[i], namespacePrefixesAndUris[i + 1]));
		}
		return new Fragment(xml, list.toArray(new Namespace[] {}));
	}

    public String getJson(String path) {
        logRequest(path, "JSON", "GET");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return getRestTemplate().exchange(buildUri(path), HttpMethod.GET, new HttpEntity<>(headers), String.class)
                .getBody();
    }

    public String getJson(URI uri) {
        logRequest(uri.toString(), "JSON", "GET");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return getRestTemplate().exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
    }

	/**
	 * Use getJsonAsSecurityUser instead.
	 *
	 * @param path
	 * @return
	 */
	@Deprecated
    public String getJsonAsAdmin(String path) {
		return getJsonAsSecurityUser(path);
    }

	public String getJsonAsSecurityUser(String path) {
		logSecurityUserRequest(path, "JSON", "GET");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return securityUserRestTemplate.exchange(buildUri(path), HttpMethod.GET, new HttpEntity<>(headers), String.class)
			.getBody();
	}

	public void delete(String path) {
        logRequest(path, "", "DELETE");
        restTemplate.delete(buildUri(path));
    }

	/**
	 * Use deleteAsSecurityUser instead.
	 *
	 * @param path
	 */
	@Deprecated
    public void deleteAsAdmin(String path) {
		deleteAsSecurityUser(path);
    }

    public void deleteAsSecurityUser(String path) {
	    logSecurityUserRequest(path, "", "DELETE");
	    securityUserRestTemplate.delete(buildUri(path));
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
        return new HttpEntity<String>(json, headers);
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
        return new HttpEntity<String>(xml, headers);
    }

    protected void logRequest(String path, String contentType, String method) {
        if (logger.isInfoEnabled()) {
        	String username = manageConfig != null ? manageConfig.getUsername() : "(unknown)";
            logger.info(String.format("Sending %s %s request as user '%s' to path: %s", contentType, method, username, path));
        }
    }

    protected void logSecurityUserRequest(String path, String contentType, String method) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Sending %s %s request as user '%s' (who must have the 'manage-admin' and 'security' roles) to path: %s",
	            contentType, method, determineUsernameForSecurityUserRequest(), path));
        }
    }

    protected String determineUsernameForSecurityUserRequest() {
	    String username = "(unknown)";
	    if (manageConfig != null) {
		    username = manageConfig.getSecurityUsername();
		    if (StringUtils.isEmpty(username)) {
			    username = manageConfig.getUsername();
		    }
	    }
	    return username;
    }

	public URI buildUri(String path) {
        return manageConfig.buildUri(path);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

	/**
	 * Use getSecurityUserRestTemplate.
	 *
	 * @return
	 */
	@Deprecated
	public RestTemplate getAdminRestTemplate() {
		return getSecurityUserRestTemplate();
    }

    public ManageConfig getManageConfig() {
        return manageConfig;
    }

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Use setSecurityUserRestTemplate.
	 *
	 * @param restTemplate
	 */
	@Deprecated
	public void setAdminRestTemplate(RestTemplate restTemplate) {
		setSecurityUserRestTemplate(restTemplate);
	}

	public RestTemplate getSecurityUserRestTemplate() {
		return securityUserRestTemplate;
	}

	public void setSecurityUserRestTemplate(RestTemplate restTemplate) {
		this.securityUserRestTemplate = restTemplate;
	}
}
