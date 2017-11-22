package com.marklogic.mgmt.resource.restapis;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.rest.util.Fragment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

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

	/**
	 * Will need to wait for MarkLogic to restart, so consider using AdminManager with this.
	 *
	 * @param request
	 * @return
	 */
	public boolean deleteRestApi(RestApiDeletionRequest request) {
		ServerManager serverManager = new ServerManager(client, request.getGroupName());
		final String serverName = request.getServerName();
		if (serverManager.exists(serverName)) {
			String path = format("/v1/rest-apis/%s", serverName);

			if (request.isIncludeContent() || request.isIncludeContent()) {
				path += "?";

				DatabaseManager databaseManager = new DatabaseManager(client);
				String payload = serverManager.getPropertiesAsJson(serverName);
				PayloadParser parser = new PayloadParser();

				if (request.isIncludeModules()) {
					if (request.isDeleteModulesReplicaForests()) {
						String modulesDatabase = parser.getPayloadFieldValue(payload, "modules-database");
						if (databaseManager.exists(modulesDatabase)) {
							databaseManager.deleteReplicaForests(modulesDatabase);
						}
					}
					path += "include=modules&";
				}

				if (request.isIncludeContent()) {
					if (request.isDeleteContentReplicaForests()) {
						String contentDatabase = parser.getPayloadFieldValue(payload, "content-database");
						if (databaseManager.exists(contentDatabase)) {
							databaseManager.deleteReplicaForests(contentDatabase);
						}
					}
					path += "include=content";
				}
			}

			logger.info("Deleting REST API, path: " + path);
			client.getRestTemplate().exchange(client.buildUri(path), HttpMethod.DELETE, null, String.class);
			logger.info("Deleted REST API");
			return true;
		} else {
			logger.info(format("Server %s does not exist, not deleting", serverName));
			return false;
		}
	}
}
