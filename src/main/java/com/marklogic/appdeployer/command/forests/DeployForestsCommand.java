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
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.hosts.DefaultHostNameProvider;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This command constructs forests based on properties found in the AppConfig object associated with the incoming
 * CommandContext. For more precise control over how forests are created, please see DeployCustomForestsCommand.
 * <p>
 * Doesn't yet support deleting forests - currently assumes that this will be done by deleting a database.
 * </p>
 * <p>
 * This class also does not support creating replica forests - these are handled by ConfigureForestReplicasCommand.
 * </p>
 */
public class DeployForestsCommand extends AbstractCommand {

	/**
	 * This was added back in 3.8.2 to preserve backwards compatibility, as it was removed in 3.7.0.
	 */
	public static final String DEFAULT_FOREST_PAYLOAD = "{\"forest-name\": \"%%FOREST_NAME%%\", \"host\": \"%%FOREST_HOST%%\", "
		+ "\"database\": \"%%FOREST_DATABASE%%\"}";

	private int forestsPerHost = 1;
	private String databaseName;
	private String forestFilename;
	private String forestPayload;

	@Deprecated
	private boolean createForestsOnEachHost = true;

	private HostCalculator hostCalculator;

	private ForestBuilder forestBuilder = new ForestBuilder();

	/**
	 * This was added back in 3.8.2 to preserve backwards compatibility, as it was removed in 3.7.0. If you use this
	 * constructor, be sure to call setDatabaseName.
	 */
	public DeployForestsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FORESTS);
	}

	/**
	 * This is the preferred constructor to use for this class, as it requires a database name, which is required for
	 * this command to execute correctly.
	 *
	 * @param databaseName
	 */
	public DeployForestsCommand(String databaseName) {
		this();
		this.databaseName = databaseName;
	}

	/**
	 * Contrary to other commands that blindly process each file in a directory, this command first looks for a specific
	 * file, as defined by the forestFilename attribute. If that file is found, then its contents are used as a
	 * template for creating forests (note that this command will determine the host, name, and database for each forest
	 * regardless of what's in the template).
	 */
	@Override
	public void execute(CommandContext context) {
		// Replicas are currently handled by ConfigureForestReplicasCommand
		List<Forest> forests = buildForests(context, false);

		if (context.getAppConfig().getCmaConfig().isDeployForests() && !forests.isEmpty() && cmaEndpointExists(context)) {
			createForestsViaCma(context, forests);
		} else {
			createForestsViaForestEndpoint(context, forests);
		}
	}

	protected void createForestsViaCma(CommandContext context, List<Forest> forests) {
		Configuration config = new Configuration();
		forests.forEach(forest -> config.addForest(forest.toObjectNode()));
		new Configurations(config).submit(context.getManageClient());
	}

	protected void createForestsViaForestEndpoint(CommandContext context, List<Forest> forests) {
		ForestManager forestManager = new ForestManager(context.getManageClient());
		for (Forest f : forests) {
			forestManager.save(f.getJson());
		}
	}

	/**
	 * Public so that it can be reused without actually saving any of the forests.
	 * <p>
	 * This also facilitates the creation of forests for many databases at one time. A client can call this on a set of
	 * these commands to construct a list of many forests that can be created via CMA in one request.
	 *
	 * @param context
	 * @param includeReplicas This command currently doesn't make use of this feature; it's here so that other clients
	 *                        can get a preview of the forests to be created, including replicas.
	 * @return
	 */
	public List<Forest> buildForests(CommandContext context, boolean includeReplicas) {
		// Need to know what primary forests exist already in case more need to be added, or a new host has been added
		List<Forest> existingPrimaryForests = null;

		// As of 4.5.3, if CMA is enabled, then the context should contain a map of all the forests for each database
		// being deployed. If it's not there, then /manage/v2 will be used instead.
		Map<String, List<Forest>> mapOfPrimaryForests = context.getMapOfPrimaryForests();
		if (mapOfPrimaryForests != null && mapOfPrimaryForests.containsKey(this.databaseName)) {
			existingPrimaryForests = mapOfPrimaryForests.get(this.databaseName);
		}

		if (existingPrimaryForests == null) {
			existingPrimaryForests = getExistingPrimaryForests(context, this.databaseName);
		}

		return buildForests(context, includeReplicas, existingPrimaryForests);
	}

	/**
	 * @param context
	 * @param includeReplicas
	 * @param existingPrimaryForests
	 * @return
	 */
	protected List<Forest> buildForests(CommandContext context, boolean includeReplicas, List<Forest> existingPrimaryForests) {
		ForestHostNames forestHostNames = determineHostNamesForForest(context, existingPrimaryForests);

		final String template = buildForestTemplate(context, new ForestManager(context.getManageClient()));

		ForestPlan forestPlan = new ForestPlan(this.databaseName, forestHostNames.getPrimaryForestHostNames())
			.withReplicaHostNames(forestHostNames.getReplicaForestHostNames())
			.withTemplate(template)
			.withForestsPerDataDirectory(this.forestsPerHost)
			.withExistingForests(existingPrimaryForests);

		if (includeReplicas) {
			Map<String, Integer> map = context.getAppConfig().getDatabaseNamesAndReplicaCounts();
			if (map != null && map.containsKey(this.databaseName)) {
				int count = map.get(this.databaseName);
				if (count > 0) {
					forestPlan.withReplicaCount(count);
				}
			}
		}

		return forestBuilder.buildForests(forestPlan, context.getAppConfig());
	}

	/**
	 * @param context
	 * @param databaseName
	 * @return
	 * @deprecated in 4.5.3, as getting forest details one at a time can be very slow for applications with a large
	 * number of forests.
	 */
	@Deprecated
	protected List<Forest> getExistingPrimaryForests(CommandContext context, String databaseName) {
		List<String> forestIds = new DatabaseManager(context.getManageClient()).getPrimaryForestIds(databaseName);
		ForestManager forestMgr = new ForestManager(context.getManageClient());
		ResourceMapper mapper = new DefaultResourceMapper(new API(context.getManageClient()));
		List<Forest> forests = new ArrayList<>();
		for (String forestId : forestIds) {
			String json = forestMgr.getPropertiesAsJson(forestId);
			forests.add(mapper.readResource(json, Forest.class));
		}
		return forests;
	}

	protected String buildForestTemplate(CommandContext context, ForestManager forestManager) {
		String payload = null;
		if (forestFilename != null) {
			for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
				File dir = configDir.getForestsDir();
				if (dir.exists()) {
					File f = new File(dir, forestFilename);
					if (f.exists()) {
						payload = copyFileToString(f);
					}
				}
			}
		}

		if (payload == null && StringUtils.hasText(forestPayload)) {
			if (logger.isInfoEnabled()) {
				logger.info("Creating forests using configured payload: " + forestPayload);
			}
			payload = forestPayload;
		}

		if (payload != null) {
			return adjustPayloadBeforeSavingResource(context, null, payload);
		}

		return null;
	}

	/**
	 * @param context
	 * @param existingPrimaryForests
	 * @return a ForestHostNames instance that defines the list of host names that can be used for primary forests and
	 * that can be used for replica forests. As of 4.1.0, the only reason these will differ is when a database is
	 * configured to only have forests on one host, or when the deprecated setCreateForestsOnOneHost method is used.
	 */
	protected ForestHostNames determineHostNamesForForest(CommandContext context, List<Forest> existingPrimaryForests) {
		if (hostCalculator == null) {
			hostCalculator = new DefaultHostCalculator(new DefaultHostNameProvider(context.getManageClient()));
		}

		// If this deprecated feature is used, then configure the AppConfig object so that the hostCalculator can be
		// aware of it.
		if (!createForestsOnEachHost) {
			context.getAppConfig().addDatabaseWithForestsOnOneHost(this.databaseName);
		}

		return hostCalculator.calculateHostNames(this.databaseName, context, existingPrimaryForests);
	}

	public int getForestsPerHost() {
		return forestsPerHost;
	}

	public void setForestsPerHost(int forestsPerHost) {
		this.forestsPerHost = forestsPerHost;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getForestFilename() {
		return forestFilename;
	}

	public void setForestFilename(String forestFilename) {
		this.forestFilename = forestFilename;
	}

	public String getForestPayload() {
		return forestPayload;
	}

	public void setForestPayload(String forestPayload) {
		this.forestPayload = forestPayload;
	}

	@Deprecated
	public boolean isCreateForestsOnEachHost() {
		return createForestsOnEachHost;
	}

	/**
	 * Use appConfig.setDatabasesWithForestsOnOneHost
	 *
	 * @param createForestsOnEachHost
	 */
	@Deprecated
	public void setCreateForestsOnEachHost(boolean createForestsOnEachHost) {
		this.createForestsOnEachHost = createForestsOnEachHost;
	}

	public void setHostCalculator(HostCalculator hostCalculator) {
		this.hostCalculator = hostCalculator;
	}

	/**
	 * This was added back in 3.8.2 to preserve backwards compatibility, as it was removed in 3.7.0.
	 *
	 * @param databaseName
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setForestBuilder(ForestBuilder forestBuilder) {
		this.forestBuilder = forestBuilder;
	}
}
