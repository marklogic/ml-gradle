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
package com.marklogic.mgmt.resource.restapis;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * For /v1/rest-apis. Currently only supports JSON files.
 */
public class RestApiManager extends LoggingObject {

	private PayloadParser payloadParser = new PayloadParser();
	private ManageClient client;
	private String groupName;

	public RestApiManager(ManageClient client) {
		this(client, ServerManager.DEFAULT_GROUP);
	}

	public RestApiManager(ManageClient client, String groupName) {
		this.client = client;
		this.groupName = groupName;
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

	/**
	 * Prior to ML 9.0-4, the /v1/rest-apis endpoint required that a server's url-rewriter have the string "rest-api"
	 * somewhere in it. With 9.0-4, the url-rewriter must match the pattern:
	 * <p>
	 * ^/MarkLogic/rest-api/(8000-rewriter|rewriter|rewriter-noxdbc)\.xml$
	 * </p>
	 * <p>
	 * It's not likely that a user's custom rewriter will fit that pattern, so this method no longer uses /v1/rest-apis,
	 * opting to use ServerManager instead.
	 * </p>
	 * <p>
	 * As of ml-app-deployer version 3.8.4, this now properly accounts for a group name.
	 * </p>
	 * @param name
	 * @return
	 */
	public boolean restApiServerExists(String name) {
		final String group = this.groupName != null ? this.groupName : ServerManager.DEFAULT_GROUP;
		return new ServerManager(this.client, group).exists(name);
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

			if (request.isIncludeModules() || request.isIncludeContent()) {
				path += "?";

				DatabaseManager databaseManager = new DatabaseManager(client);
				String payload = serverManager.getPropertiesAsJson(serverName);
				PayloadParser parser = new PayloadParser();

				if (request.isIncludeModules()) {
					boolean includeModules = true;
					if (request.isDeleteModulesReplicaForests()) {
						String modulesDatabase = null;
						try {
							modulesDatabase = parser.getPayloadFieldValue(payload, "modules-database");
						} catch (Exception e) {
							logger.warn("Unable to get value of `modules-database`; will not be able to delete " +
								"modules database. This may be expected if the modules database has been set to " +
								"'filesystem' for the app server. Error: {}", e.getMessage());
							includeModules = false;
						}
						if (modulesDatabase != null && databaseManager.exists(modulesDatabase)) {
							databaseManager.deleteReplicaForests(modulesDatabase);
						}
					}
					if (includeModules) {
						path += "include=modules&";
					}
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
