package com.marklogic.appdeployer.mgmt;

import org.jdom2.Namespace;

import com.marklogic.appdeployer.util.Fragment;
import com.marklogic.clientutil.LoggingObject;

/**
 * For /v1/rest-apis.
 */
public class ServiceManager extends LoggingObject {

    private ManageClient client;

    public ServiceManager(ManageClient client) {
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
        String xml = client.getRestTemplate().getForObject(client.getBaseUrl() + "/v1/rest-apis?format=xml",
                String.class);
        Fragment f = new Fragment(xml, Namespace.getNamespace("rapi", "http://marklogic.com/rest-api"));
        return f.elementExists(String.format("/rapi:rest-apis/rapi:rest-api[rapi:name = '%s']", name));
    }
}
