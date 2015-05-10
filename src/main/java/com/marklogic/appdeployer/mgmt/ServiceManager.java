package com.marklogic.appdeployer.mgmt;

import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.util.Fragment;

/**
 * For /v1/rest-apis.
 */
public class ServiceManager extends GenericManager {

    public ServiceManager(RestTemplate restTemplate, String baseUrl) {
        super(restTemplate, baseUrl);
    }

    public void createRestApi(String name, String json) {
        if (restApiServerExists(name)) {
            logger.info("REST API server already exists with name: " + name);
        } else {
            logger.info("Creating REST API: " + json);
            postJson("/v1/rest-apis", json);
            logger.info("Created REST API");
        }
    }

    public boolean restApiServerExists(String name) {
        String xml = getRestTemplate().getForObject(getBaseUrl() + "/v1/rest-apis?format=xml", String.class);
        Fragment f = new Fragment(xml);
        f.prettyPrint();
        return true;
    }
}
