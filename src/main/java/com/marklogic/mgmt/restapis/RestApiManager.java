package com.marklogic.mgmt.restapis;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.rest.util.Fragment;

/**
 * For /v1/rest-apis. Currently only supports JSON files.
 */
public class RestApiManager extends LoggingObject {

    private PayloadParser payloadParser = new PayloadParser();
    private ManageClient client;

    public RestApiManager(ManageClient client) {
        this.client = client;
    }

    public ResponseEntity<String> createRestApi(String json) {
        return createRestApi(extractNameFromJson(json), json);
    }

    public ResponseEntity<String> createRestApi(String name, String json) {
        logger.info("Checking for existence of REST API with name: " + name);
        if (restApiServerExists(name)) {
            logger.info("REST API server already exists with name: " + name);
            return null;
        } else {
            logger.info("Creating REST API: " + json);
            ResponseEntity<String> re = client.postJson("/v1/rest-apis", json);
            logger.info("Created REST API");
            return re;
        }
    }

    public String extractNameFromJson(String json) {
        JsonNode node = payloadParser.parseJson(json);
        return node.get("rest-api").get("name").textValue();
    }

    public boolean restApiServerExists(String name) {
        Fragment f = client.getXml("/v1/rest-apis?format=xml", "rapi", "http://marklogic.com/rest-api");
        return f.elementExists(String.format("/rapi:rest-apis/rapi:rest-api[rapi:name = '%s']", name));
    }
}
