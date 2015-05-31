package com.marklogic.appdeployer.mgmt;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Namespace;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.util.Fragment;
import com.marklogic.appdeployer.util.RestTemplateUtil;
import com.marklogic.clientutil.LoggingObject;

/**
 * Wraps a RestTemplate with methods that should simplify accessing the Manage API with RestTemplate. Each NounManager
 * should depend on an instance of ManageClient for accessing the Manage API.
 */
public class ManageClient extends LoggingObject {

    private RestTemplate restTemplate;
    private String baseUrl;

    public ManageClient(ManageConfig config) {
        initialize(config);
    }

    public void initialize(ManageConfig config) {
        if (logger.isInfoEnabled()) {
            logger.info("Initializing with manage config of: " + config);
        }
        this.restTemplate = RestTemplateUtil.newRestTemplate(config);
        this.baseUrl = config.getBaseUrl();
        if (logger.isInfoEnabled()) {
            logger.info("Initialized with base URL of: " + baseUrl);
        }
    }

    public ResponseEntity<String> putJson(String path, String json) {
        return restTemplate.exchange(baseUrl + path, HttpMethod.PUT, buildJsonEntity(json), String.class);
    }

    public ResponseEntity<String> postJson(String path, String json) {
        return restTemplate.exchange(baseUrl + path, HttpMethod.POST, buildJsonEntity(json), String.class);
    }

    protected HttpEntity<String> buildJsonEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<String>(json, headers);
    }

    public ResponseEntity<String> postForm(String path, String... params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        for (int i = 0; i < params.length; i += 2) {
            map.add(params[i], params[i + 1]);
        }
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.exchange(baseUrl + path, HttpMethod.POST, entity, String.class);
    }

    public Fragment getXml(String path, String... namespacePrefixesAndUris) {
        String xml = getRestTemplate().getForObject(getBaseUrl() + path, String.class);
        List<Namespace> list = new ArrayList<Namespace>();
        for (int i = 0; i < namespacePrefixesAndUris.length; i += 2) {
            list.add(Namespace.getNamespace(namespacePrefixesAndUris[i], namespacePrefixesAndUris[i + 1]));
        }
        return new Fragment(xml, list.toArray(new Namespace[] {}));
    }

    public void delete(String path) {
        restTemplate.delete(baseUrl + path);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
