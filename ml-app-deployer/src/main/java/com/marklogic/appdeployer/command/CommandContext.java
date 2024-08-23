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
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the context within which a command executes, which includes:
 *
 * <ul>
 * <li>An AppConfig object that defines configuration values for an application</li>
 * <li>A ManageClient for connecting to the Manage API</li>
 * <li>An AdminManager for performing operations against the Admin app server</li>
 * <li>A context map that commands are free to store anything they wish within</li>
 * </ul>
 */
public class CommandContext {

	private AppConfig appConfig;
	private ManageClient manageClient;
	private AdminManager adminManager;

	private Map<String, Object> contextMap;

	private final static String COMBINED_CMA_REQUEST_KEY = "cma-combined-request";

	public CommandContext(AppConfig appConfig, ManageClient manageClient, AdminManager adminManager) {
		super();
		this.appConfig = appConfig;
		this.manageClient = manageClient;
		this.adminManager = adminManager;
		this.contextMap = new HashMap<>();
	}

	public void addCmaConfigurationToCombinedRequest(Configuration configuration) {
		Configurations configs = getCombinedCmaRequest();
		if (configs == null) {
			contextMap.put(COMBINED_CMA_REQUEST_KEY, new Configurations(configuration));
		} else {
			configs.addConfig(configuration);
		}
	}

	/**
	 * Added to greatly speed up performance when getting details about all the existing primary forests for each
	 * database in the cluster. In the event that anything fails while getting the forest details, the map won't be
	 * added to the context and any code expecting to use the map will have to fall back to using /manage/v2.
	 *
	 * @since 4.5.3
	 */
	public Map<String, List<Forest>> getMapOfPrimaryForests() {
		if (!appConfig.getCmaConfig().isDeployForests()) {
			return null;
		}
		final String key = "ml-app-deployer-mapOfPrimaryForests";
		if (contextMap.containsKey(key)) {
			return (Map<String, List<Forest>>) contextMap.get(key);
		}
		Logger logger = LoggerFactory.getLogger(getClass());
		try {
			logger.info("Retrieving all forest details via CMA");
			long start = System.currentTimeMillis();
			Map<String, List<Forest>> mapOfPrimaryForests = new ForestManager(manageClient).getMapOfPrimaryForests();
			logger.info("Finished retrieving all forests details via CMA; duration: " + (System.currentTimeMillis() - start));
			contextMap.put(key, mapOfPrimaryForests);
			return mapOfPrimaryForests;
		} catch (Exception ex) {
			logger.warn("Unable to retrieve all forest details, cause: " + ex.getMessage() + "; will fall back to " +
				"using /manage/v2 when needed for getting details for a forest.");
			return null;
		}
	}

	public Configurations getCombinedCmaRequest() {
		return (Configurations) contextMap.get(COMBINED_CMA_REQUEST_KEY);
	}

	public void removeCombinedCmaRequest() {
		contextMap.remove(COMBINED_CMA_REQUEST_KEY);
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public ManageClient getManageClient() {
		return manageClient;
	}

	public AdminManager getAdminManager() {
		return adminManager;
	}

	public Map<String, Object> getContextMap() {
		return contextMap;
	}

	/**
	 * @param contextMap
	 * @deprecated since 4.6.0, will be removed in 5.0.0; the contextMap is not intended to be replaced.
	 */
	@Deprecated
	public void setContextMap(Map<String, Object> contextMap) {
		this.contextMap = contextMap;
	}
}
