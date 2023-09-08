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
package com.marklogic.mgmt.resource.forests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.cma.ConfigurationManager;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.Fragment;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides methods wrapping /manage/v2/forests endpoints.
 * <p>
 * The "save" method inherited from the parent class does not allow updates, as updates can fail for a variety of
 * reasons with forests - e.g. if the host is different. If you need to update a forest, consider just using
 * ManageClient to do it.
 */
public class ForestManager extends AbstractResourceManager {

	// Parameter values for the "level" parameter when deleting a forest
	public final static String DELETE_LEVEL_FULL = "full";
	public final static String DELETE_LEVEL_CONFIG_ONLY = "config-only";

	// Parameter values for the "replicas" parameter when deleting a forest
	public final static String REPLICAS_DETACH = "detach";
	public final static String REPLICAS_DELETE = "delete";

	private String deleteLevel = DELETE_LEVEL_FULL;
	private int deleteRetryAttempts = 3;
	private long deleteSleepPeriod = 500;

	public ForestManager(ManageClient client) {
		super(client);
		setUpdateAllowed(false);
	}

	/**
	 * Uses CMA to get back all forests in the cluster. Then returns a map where keys are the database names,
	 * and each key has a list of primary forests for that database.
	 *
	 * @return
	 * @since 4.5.3
	 */
	public Map<String, List<Forest>> getMapOfPrimaryForests() {
		JsonNode json = new ConfigurationManager(getManageClient()).getResourcesAsJson("forest").getBody();
		// Config is an array of objects, and it will have a single object based on our request.
		ArrayNode allPrimaryForests = (ArrayNode) json.get("config").get(0).get("forest");
		ResourceMapper mapper = new DefaultResourceMapper(new API(getManageClient()));

		Map<String, List<Forest>> mapOfPrimaryForests = new HashMap<>();
		allPrimaryForests.iterator().forEachRemaining(forest -> {
			if (forest.has("database")) {
				String db = forest.get("database").asText();
				List<Forest> databaseForests = mapOfPrimaryForests.get(db);
				if (databaseForests == null) {
					databaseForests = new ArrayList<>();
					mapOfPrimaryForests.put(db, databaseForests);
				}
				databaseForests.add(mapper.readResource(forest.toString(), Forest.class));
			}
		});

		return mapOfPrimaryForests;
	}

	public void createJsonForestWithName(String name, String host) {
		if (forestExists(name)) {
			logger.info(format("Forest already exists with name, so not creating: %s", name));
		} else {
			logger.info(format("Creating forest %s on host %s", name, host));
			createJsonForest(format("{\"forest-name\":\"%s\", \"host\":\"%s\"}", name, host));
			logger.info(format("Created forest %s on host %s", name, host));
		}
	}

	public void delete(String nameOrId, String level) {
		delete(nameOrId, level, REPLICAS_DELETE);
	}

	/**
	 * @param nameOrId Name or ID of forest to delete
	 * @param level    either "full" or "config-only"
	 * @param replicas either "detach" or "delete"
	 */
	public void delete(String nameOrId, String level, String replicas) {
		if (!forestExists(nameOrId)) {
			logger.info(format("Could not find forest with name or ID: %s, so not deleting", nameOrId));
		} else {
			logger.info(format("Deleting forest %s", nameOrId));
			String path = format("/manage/v2/forests/%s?level=%s&replicas=%s", nameOrId, level, replicas);
			deleteWithRetry(path, deleteRetryAttempts);
			logger.info(format("Deleted forest %s", nameOrId));
		}
	}

	public void deleteWithRetry(String path, int attemptsLeft) {
		try {
			getManageClient().delete(path);
		} catch (Exception e) {
			// Error will be logged automatically by MgmtResponseErrorHandler
			if (attemptsLeft > 0) {
				try {
					logger.warn("Unable to delete forest; will wait " + deleteSleepPeriod + "ms, then retry at path: " + path);
					Thread.sleep(deleteSleepPeriod);
					attemptsLeft--;
					deleteWithRetry(path, attemptsLeft);
				} catch (InterruptedException ex) {
					// Ignore
				}
			} else {
				throw e;
			}
		}
	}

	/**
	 * Supports either an array of JSON objects or a single JSON object.
	 *
	 * @param json
	 */
	public void saveJsonForests(String json) {
		JsonNode node = super.payloadParser.parseJson(json);
		if (node.isArray()) {
			Iterator<JsonNode> iter = node.iterator();
			while (iter.hasNext()) {
				save(iter.next().toString());
			}
		} else {
			save(json);
		}
	}

	public void createJsonForest(String json) {
		getManageClient().postJson("/manage/v2/forests", json);
	}

	public boolean forestExists(String nameOrId) {
		Fragment f = getManageClient().getXml("/manage/v2/forests");
		return f.elementExists(format("/node()/f:list-items/f:list-item[f:nameref = '%s' or f:idref = '%s']", nameOrId,
			nameOrId));
	}

	public void attachForest(String forestIdOrName, String databaseIdOrName) {
		if (isForestAttached(forestIdOrName)) {
			logger.info(format("Forest %s is already attached to a database, not attaching", forestIdOrName));
			return;
		}
		logger.info(format("Attaching forest %s to database %s", forestIdOrName, databaseIdOrName));
		String path = format("/manage/v2/forests/%s", forestIdOrName);
		getManageClient().postForm(path, "state", "attach", "database", databaseIdOrName);
		logger.info(format("Attached forest %s to database %s", forestIdOrName, databaseIdOrName));
	}

	public boolean isForestAttached(String forestIdOrName) {
		Fragment f = getManageClient().getXml(format("/manage/v2/forests/%s", forestIdOrName));
		return f.elementExists("/node()/f:relations/f:relation-group[f:typeref = 'databases']");
	}

	public String getHostId(String forestIdOrName) {
		Fragment f = getManageClient().getXml(format("/manage/v2/forests/%s", forestIdOrName));
		return f.getElementValue("/node()/f:relations/f:relation-group[f:typeref = 'hosts']/f:relation/f:idref");
	}

	/**
	 * @param forestIdOrName
	 * @param replicaNamesAndHostIds A map where each key is a replica forest name, and its value is the host ID of that forest
	 */
	public void setReplicas(String forestIdOrName, Map<String, String> replicaNamesAndHostIds) {
		StringBuilder json = new StringBuilder("{\"forest-replica\":[");
		boolean firstOne = true;
		for (String replicaName : replicaNamesAndHostIds.keySet()) {
			if (!firstOne) {
				json.append(",");
			}
			String hostId = replicaNamesAndHostIds.get(replicaName);
			json.append(format("{\"replica-name\":\"%s\", \"host\":\"%s\"}", replicaName, hostId));
			firstOne = false;
		}
		json.append("]}");
		if (logger.isInfoEnabled()) {
			logger.info(format("Setting replicas for forest %s, JSON: %s", forestIdOrName, json.toString()));
		}
		getManageClient().putJson(getPropertiesPath(forestIdOrName), json.toString());
		if (logger.isInfoEnabled()) {
			logger.info(format("Finished setting replicas for forest %s", forestIdOrName));
		}
	}

	/**
	 * Convenience method for detaching a forest from any replicas it has; this is often used before deleting those
	 * replicas
	 *
	 * @param forestIdOrName
	 */
	public void setReplicasToNone(String forestIdOrName) {
		setReplicas(forestIdOrName, new HashMap<>());
	}

	/**
	 * Returns a list of IDs for each replica forest for the given forest ID or name.
	 *
	 * @param forestIdOrName
	 * @return
	 */
	public List<String> getReplicaIds(String forestIdOrName) {
		String path = getResourcePath(forestIdOrName, "view", "config", "format", "xml");
		return getManageClient().getXml(path).getElementValues(
			"/f:forest-config/f:config-properties/f:forest-replicas/f:forest-replica");
	}

	/**
	 * Deletes (with a level of "full") all replicas for the given forest.
	 *
	 * @param forestIdOrName
	 */
	public void deleteReplicas(String forestIdOrName) {
		List<String> replicaIds = getReplicaIds(forestIdOrName);
		if (replicaIds.isEmpty()) {
			logger.info(format("Forest '%s' has no replicas, so not deleting anything", forestIdOrName));
			return;
		}
		setReplicasToNone(forestIdOrName);
		for (String id : replicaIds) {
			delete(id, DELETE_LEVEL_FULL);
		}
	}

	public ForestStatus getForestStatus(String forestIdOrName) {
		String path = getResourcePath(forestIdOrName, "view", "status", "format", "xml");
		return new ForestStatus(getManageClient().getXml(path));
	}

	public void setUpdatesAllowed(String forestIdOrName, String mode) {
		String path = getPropertiesPath(forestIdOrName);
		String json = format("{\"updates-allowed\":\"%s\"}", mode);
		getManageClient().putJson(path, json);
	}

	@Override
	protected String[] getDeleteResourceParams(String payload) {
		return this.deleteLevel != null ? new String[]{"level", deleteLevel} : null;
	}

	public String getDeleteLevel() {
		return deleteLevel;
	}

	public void setDeleteLevel(String deleteLevel) {
		this.deleteLevel = deleteLevel;
	}

	public void setDeleteRetryAttempts(int deleteRetryAttempts) {
		this.deleteRetryAttempts = deleteRetryAttempts;
	}

	public void setDeleteSleepPeriod(long deleteSleepPeriod) {
		this.deleteSleepPeriod = deleteSleepPeriod;
	}
}
