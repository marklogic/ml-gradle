package com.rjrudin.marklogic.mgmt.restapis;

import com.rjrudin.marklogic.client.LoggingObject;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.rest.util.Fragment;

/**
 * For /v1/rest-apis.
 */
public class RestApiManager extends LoggingObject {

    private ManageClient client;

    public RestApiManager(ManageClient client) {
        this.client = client;
    }

    public void createRestApi(String name, String json) {
        logger.info("Checking for existence of REST API with name: " + name);
        if (restApiServerExists(name)) {
            logger.info("REST API server already exists with name: " + name);
        } else {
            logger.info("Creating REST API: " + json);
            client.postJson("/v1/rest-apis", json);
            logger.info("Created REST API");
        }
    }

    public boolean restApiServerExists(String name) {
        Fragment f = client.getXml("/v1/rest-apis?format=xml", "rapi", "http://marklogic.com/rest-api");
        return f.elementExists(String.format("/rapi:rest-apis/rapi:rest-api[rapi:name = '%s']", name));
    }
}
