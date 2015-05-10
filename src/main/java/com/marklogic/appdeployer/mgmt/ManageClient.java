package com.marklogic.appdeployer.mgmt;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.marklogic.clientutil.LoggingObject;

/**
 * Wraps a RestTemplate with methods that should simplify accessing the Manage API with RestTemplate.
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
        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(config.getHost(), config.getPort(), AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        this.baseUrl = config.getBaseUrl();
        if (logger.isInfoEnabled()) {
            logger.info("Initialized with base URL of: " + baseUrl);
        }
    }

    public ResponseEntity<String> postJson(String path, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        return restTemplate.exchange(baseUrl + path, HttpMethod.POST, request, String.class);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
