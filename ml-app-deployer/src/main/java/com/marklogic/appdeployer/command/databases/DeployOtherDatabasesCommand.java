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
package com.marklogic.appdeployer.command.databases;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.forests.DeployForestsCommand;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.database.DatabaseSorter;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.JsonNodeUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * As of release 3.14.0, this now handles all databases, not just "databases other than the default content database".
 * Its name will be changed to DeployDatabasesCommand in a future release, which likely will also be when
 * DeployContentDatabaseCommand is deleted (probably 4.0).
 */
public class DeployOtherDatabasesCommand extends AbstractUndoableCommand {

	// Each of these is copied to the instances of DeployDatabaseCommand that are created
	private Integer forestsPerHost;
	private boolean checkForCustomForests = true;
	private String forestFilename;
	private boolean createForestsOnEachHost = true;

	/**
	 * Defines database names that, by default, this command will never undeploy.
	 */
	private Set<String> defaultDatabasesToNotUndeploy = new HashSet<>();

	private DeployDatabaseCommandFactory deployDatabaseCommandFactory = new DefaultDeployDatabaseCommandFactory();

	private PayloadParser payloadParser = new PayloadParser();

	public DeployOtherDatabasesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_DATABASES);
		setUndoSortOrder(SortOrderConstants.DELETE_OTHER_DATABASES);
		setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_DATABASES);
		setUndoSortOrder(SortOrderConstants.DELETE_OTHER_DATABASES);
		initializeDefaultDatabasesToNotUndeploy();
		setResourceClassType(Database.class);
	}

	public DeployOtherDatabasesCommand(int forestsPerHost) {
		this();
		setForestsPerHost(forestsPerHost);
	}

	protected void initializeDefaultDatabasesToNotUndeploy() {
		defaultDatabasesToNotUndeploy = new HashSet<>();
		defaultDatabasesToNotUndeploy.add("App-Services");
		defaultDatabasesToNotUndeploy.add("Documents");
		defaultDatabasesToNotUndeploy.add("Extensions");
		defaultDatabasesToNotUndeploy.add("Fab");
		defaultDatabasesToNotUndeploy.add("Last-Login");
		defaultDatabasesToNotUndeploy.add("Meters");
		defaultDatabasesToNotUndeploy.add("Modules");
		defaultDatabasesToNotUndeploy.add("Schemas");
		defaultDatabasesToNotUndeploy.add("Security");
		defaultDatabasesToNotUndeploy.add("Triggers");
	}

	/**
	 * Deploys each of the databases found via buildDatabasePlans.
	 *
	 * @param context
	 */
	@Override
	public void execute(CommandContext context) {
		List<DatabasePlan> databasePlans = buildDatabasePlans(context);

		if (context.getAppConfig().isSortOtherDatabaseByDependencies()) {
			databasePlans = sortDatabasePlans(databasePlans);
		} else {
			logger.info("Not sorting databases by dependencies, will sort them by their filenames instead");
		}

		if (context.getAppConfig().getCmaConfig().isDeployDatabases()) {
			deployDatabasesAndForestsViaCma(context, databasePlans);
		} else {
			// Otherwise, create each database one at a time, which also handles sub-databases
			databasePlans.forEach(databasePlan -> {
				databasePlan.getDeployDatabaseCommand().execute(context);
			});

			// Either create forests in one bulk CMA request, or via a command per database
			if (context.getAppConfig().getCmaConfig().isDeployForests()) {
				deployAllForestsInSingleCmaRequest(context, databasePlans);
			} else {
				databasePlans.forEach(databasePlan -> {
					DeployForestsCommand dfc = databasePlan.getDeployDatabaseCommand().getDeployForestsCommand();
					if (dfc != null) {
						dfc.execute(context);
					}
				});
			}
		}
	}

	/**
	 * Undeploys each of the databases found via buildDatabasePlans.
	 *
	 * @param context
	 */
	@Override
	public void undo(CommandContext context) {
		List<DatabasePlan> databasePlans = buildDatabasePlans(context);

		if (context.getAppConfig().isSortOtherDatabaseByDependencies()) {
			databasePlans = sortDatabasePlans(databasePlans);
			Collections.reverse(databasePlans);
		} else {
			logger.info("Not sorting databases by dependencies, will sort them by their filenames instead");
		}

		databasePlans.forEach(databasePlan -> {
			databasePlan.getDeployDatabaseCommand().undo(context);
		});

		// If no databases were found, may still need to delete the content database in case no file exists for it.
		// That's because the command for creating a REST API server will not delete the content database by default,
		// though it will delete the test database by default
		if (deleteContentDatabaseOnUndo(databasePlans, context.getAppConfig())) {
			DatabaseManager dbMgr = new DeployDatabaseCommand().newDatabaseManageForDeleting(context);
			dbMgr.deleteByName(context.getAppConfig().getContentDatabaseName());
		}
	}

	/**
	 * If no database files are found, may still need to delete the content database in case no file exists for it.
	 * That's because the command for creating a REST API server will not delete the content database by default.
	 *
	 * Per ticket #404, this will now do a check to see if the default content database filename is ignored. If so,
	 * and there are no database files found, then the content database will not be deleted.
	 *
	 * @param databasePlans
	 * @param appConfig
	 * @return
	 */
	protected boolean deleteContentDatabaseOnUndo(List<DatabasePlan> databasePlans, AppConfig appConfig) {
		if (databasePlans == null || databasePlans.isEmpty()) {
			FilenameFilter filter = getResourceFilenameFilter();
			if (filter != null && filter instanceof ResourceFilenameFilter) {
				Set<String> filenamesToIgnore = ((ResourceFilenameFilter) filter).getFilenamesToIgnore();
				if (filenamesToIgnore != null && !filenamesToIgnore.isEmpty() && appConfig.getConfigDirs() != null) {
					for (ConfigDir configDir : appConfig.getConfigDirs()) {
						if (filenamesToIgnore.contains(configDir.getDefaultContentDatabaseFilename())) {
							return false;
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Performs all the work of finding all the database files across all the configuration paths; merging matching
	 * database files together; and then building an instance of DeployDatabaseCommand for each database that needs
	 * to be deployed. This method doesn't make any calls to deploy/undeploy databases though - it just builds up all
	 * the data that is needed to do so.
	 * <p>
	 * This is public so that ml-gradle can invoke it when previewing what forests will be created for a database.
	 *
	 * @param context
	 * @return
	 */
	public List<DatabasePlan> buildDatabasePlans(CommandContext context) {
		DatabasePlans databasePlan = new DatabasePlans();

		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			addDatabaseFiles(context, configDir, databasePlan);
		}

		List<DatabasePlan> databasePlans = mergeDatabasePlanFiles(context, databasePlan);
		buildDeployDatabaseCommands(context, databasePlans);

		if (logger.isDebugEnabled()) {
			logger.debug("Logging the files for each database before it's created or updated:");
			databasePlans.forEach(plan -> logger.debug(plan + "\n"));
		}

		return databasePlans;
	}

	/**
	 * @param context
	 * @param configDir
	 * @param databasePlans
	 */
	protected void addDatabaseFiles(CommandContext context, ConfigDir configDir, DatabasePlans databasePlans) {
		final String contentDatabaseFilename = configDir.getDefaultContentDatabaseFilename();
		File dir = configDir.getDatabasesDir();
		if (dir != null && dir.exists()) {
			for (File f : listFilesInDirectory(dir)) {
				String payload = copyFileToString(f, context);
				String databaseName = payloadParser.getPayloadFieldValue(payload, "database-name", false);
				if (databaseName != null) {
					boolean isMainContentDatabase = false;
					if (databasePlans.getMainContentDatabaseName() == null && f.getName().equals(contentDatabaseFilename)) {
						databasePlans.setMainContentDatabaseName(databaseName);
						isMainContentDatabase = true;
					}
					if (databasePlans.getDatabasePlanMap().containsKey(databaseName)) {
						DatabasePlan reference = databasePlans.getDatabasePlanMap().get(databaseName);
						reference.addFile(f);
						if (!reference.isMainContentDatabase() && isMainContentDatabase) {
							reference.setMainContentDatabase(isMainContentDatabase);
						}
					} else {
						databasePlans.getDatabasePlanMap().put(databaseName, new DatabasePlan(databaseName, f, isMainContentDatabase));
					}
				}
			}
		} else {
			logResourceDirectoryNotFound(dir);
		}
	}

	/**
	 * For each DatabasePlan in the DatabasePlan, the files (usually just one) are merged together if needed and
	 * then stored as the payload on the given DatabasePlan. In addition, a check is made to see if a test
	 * database should be created that mirrors the main content database.
	 *
	 * @param context
	 * @param databasePlans
	 * @return
	 */
	protected List<DatabasePlan> mergeDatabasePlanFiles(CommandContext context, DatabasePlans databasePlans) {
		ResourceMapper resourceMapper = new DefaultResourceMapper(new API(context.getManageClient()));
		ObjectReader objectReader = ObjectMapperFactory.getObjectMapper().readerFor(Database.class);

		List<DatabasePlan> databasePlanList = new ArrayList<>();
		databasePlanList.addAll(databasePlans.getDatabasePlanMap().values());

		DatabasePlan testDatabasePlan = null;

		final String testContentDatabaseName = context.getAppConfig().getTestContentDatabaseName();

		for (DatabasePlan reference : databasePlanList) {
			boolean createTestDatabase = reference.isMainContentDatabase() && context.getAppConfig().isTestPortSet();
			if (createTestDatabase) {
				testDatabasePlan = new DatabasePlan(testContentDatabaseName, reference.getFiles());
			}

			List<File> files = reference.getFiles();
			if (files.size() == 1) {
				String payload = copyFileToString(files.get(0), context);
				reference.setPayload(payload);
				reference.setDatabaseForSorting(resourceMapper.readResource(payload, Database.class));
				if (createTestDatabase) {
					String testPayload = payloadTokenReplacer.replaceTokens(copyFileToString(files.get(0)), context.getAppConfig(), true);
					testDatabasePlan.setPayload(testPayload);
					Database testDb = resourceMapper.readResource(payload, Database.class);
					testDb.setDatabaseName(testContentDatabaseName);
					testDatabasePlan.setDatabaseForSorting(testDb);
				}
			} else {
				List<ObjectNode> nodes = new ArrayList<>();
				files.forEach(file -> {
					String payload = copyFileToString(file, context);
					nodes.add(convertPayloadToObjectNode(context, payload));
				});
				ObjectNode mergedNode = JsonNodeUtil.mergeObjectNodes(nodes.toArray(new ObjectNode[]{}));
				reference.setMergedObjectNode(mergedNode);
				try {
					reference.setDatabaseForSorting(objectReader.readValue(mergedNode));
				} catch (IOException e) {
					throw new RuntimeException("Unable to read ObjectNode into Database class, cause: " + e.getMessage(), e);
				}

				if (createTestDatabase) {
					List<ObjectNode> testNodes = new ArrayList<>();
					files.forEach(file -> {
						String testPayload = payloadTokenReplacer.replaceTokens(copyFileToString(file), context.getAppConfig(), true);
						testNodes.add(convertPayloadToObjectNode(context, testPayload));
					});
					ObjectNode mergedTestNode = JsonNodeUtil.mergeObjectNodes(testNodes.toArray(new ObjectNode[]{}));
					testDatabasePlan.setMergedObjectNode(mergedTestNode);
					try {
						testDatabasePlan.setDatabaseForSorting(objectReader.readValue(mergedTestNode));
					} catch (IOException e) {
						throw new RuntimeException("Unable to read ObjectNode into Database class, cause: " + e.getMessage(), e);
					}
				}
			}
		}

		if (testDatabasePlan != null) {
			databasePlanList.add(testDatabasePlan);
			testDatabasePlan.setTestContentDatabase(true);
		}

		return databasePlanList;
	}

	protected List<DatabasePlan> sortDatabasePlans(List<DatabasePlan> databasePlans) {
		List<Database> databases = new ArrayList<>();
		Map<String, DatabasePlan> map = new HashMap<>();
		databasePlans.forEach(plan -> {
			databases.add(plan.getDatabaseForSorting());
			map.put(plan.getDatabaseName(), plan);
		});

		String[] sortedNames = new DatabaseSorter().sortDatabasesAndReturnNames(databases);

		List<DatabasePlan> sortedList = new ArrayList<>();
		for (String name : sortedNames) {
			sortedList.add(map.get(name));
		}
		return sortedList;
	}

	/**
	 * For each DatabasePlan, build a DeployDatabaseCommand that can later be executed for the database.
	 *
	 * @param databasePlans
	 */
	protected void buildDeployDatabaseCommands(CommandContext context, List<DatabasePlan> databasePlans) {
		databasePlans.forEach(databasePlan -> {
			DeployDatabaseCommand command = deployDatabaseCommandFactory.newDeployDatabaseCommand(databasePlan.getLastFile());
			command.setCheckForCustomForests(isCheckForCustomForests());
			command.setCreateForestsOnEachHost(isCreateForestsOnEachHost());
			command.setDatabasesToNotUndeploy(this.getDefaultDatabasesToNotUndeploy());

			if (databasePlan.isMainContentDatabase() || databasePlan.isTestContentDatabase()) {
				Integer contentForestsPerHost = context.getAppConfig().getContentForestsPerHost();
				if (contentForestsPerHost != null) {
					command.setForestsPerHost(contentForestsPerHost);
				} else if (this.forestsPerHost != null) {
					command.setForestsPerHost(this.forestsPerHost);
				} else {
					command.setForestsPerHost(3); // default as defined by /v1/rest-apis
				}
				command.setForestFilename("content-forest.json");
			} else {
				if (this.forestsPerHost != null) {
					command.setForestsPerHost(this.forestsPerHost);
				}
				command.setForestFilename(getForestFilename());
			}

			// Set the payload so the command doesn't try to generate it
			command.setPayload(databasePlan.getPayload());

			command.setPostponeForestCreation(context.getAppConfig().getCmaConfig().isDeployForests());
			databasePlan.setDeployDatabaseCommand(command);
		});
	}

	/**
	 * As of 3.15.0, if databases are to be deployed via CMA, then their forests will also be deployed via CMA,
	 * regardless of the setting on the AppConfig instance.
	 * <p>
	 * Also as of 3.15.0, sub-databases and their forests are never deployed by CMA. Will support this in a future
	 * release.
	 *
	 * @param context
	 * @param databasePlans
	 */
	protected void deployDatabasesAndForestsViaCma(CommandContext context, List<DatabasePlan> databasePlans) {
		Configuration dbConfig = new Configuration();
		// Forests must be included in a separate configuration object
		Configuration forestConfig = new Configuration();

		databasePlans.forEach(plan -> {
			final DeployDatabaseCommand deployDatabaseCommand = plan.getDeployDatabaseCommand();
			String payload = deployDatabaseCommand.buildPayloadForSaving(context);
			dbConfig.addDatabase(convertPayloadToObjectNode(context, payload));

			DeployForestsCommand deployForestsCommand = deployDatabaseCommand.buildDeployForestsCommand(plan.getDatabaseName(), context);
			if (deployForestsCommand != null) {
				deployForestsCommand.buildForests(context, false).forEach(forest -> forestConfig.addForest(forest.toObjectNode()));
			}
		});

		new Configurations(dbConfig, forestConfig).submit(context.getManageClient());

		// Now account for sub-databases, but not yet (as of 3.15.0) with CMA
		databasePlans.forEach(plan -> {
			plan.getDeployDatabaseCommand().deploySubDatabases(plan.getDatabaseName(), context);
		});
	}

	/**
	 * Each DatabasePlan is expected to have constructed a DeployForestCommand, but not executed it. Each
	 * DeployForestCommand can then be used to build a list of forests. All of those forests can be combined into a
	 * single list and then submitted to CMA, thereby greatly speeding up the creation of the forests.
	 *
	 * @param context
	 * @param databasePlans
	 */
	protected void deployAllForestsInSingleCmaRequest(CommandContext context, List<DatabasePlan> databasePlans) {
		List<Forest> allForests = new ArrayList<>();
		databasePlans.forEach(plan -> {
			DeployForestsCommand dfc = plan.getDeployDatabaseCommand().getDeployForestsCommand();
			if (dfc != null) {
				allForests.addAll(dfc.buildForests(context, false));
			}
		});
		if (!allForests.isEmpty()) {
			Configuration config = new Configuration();
			allForests.forEach(forest -> config.addForest(forest.toObjectNode()));
			new Configurations(config).submit(context.getManageClient());
		}
	}

	public void setDeployDatabaseCommandFactory(DeployDatabaseCommandFactory deployDatabaseCommandFactory) {
		this.deployDatabaseCommandFactory = deployDatabaseCommandFactory;
	}

	public Integer getForestsPerHost() {
		return forestsPerHost;
	}

	public void setForestsPerHost(Integer forestsPerHost) {
		this.forestsPerHost = forestsPerHost;
	}

	public boolean isCheckForCustomForests() {
		return checkForCustomForests;
	}

	public void setCheckForCustomForests(boolean checkForCustomForests) {
		this.checkForCustomForests = checkForCustomForests;
	}

	public String getForestFilename() {
		return forestFilename;
	}

	public void setForestFilename(String forestFilename) {
		this.forestFilename = forestFilename;
	}

	public boolean isCreateForestsOnEachHost() {
		return createForestsOnEachHost;
	}

	public void setCreateForestsOnEachHost(boolean createForestsOnEachHost) {
		this.createForestsOnEachHost = createForestsOnEachHost;
	}

	public Set<String> getDefaultDatabasesToNotUndeploy() {
		return defaultDatabasesToNotUndeploy;
	}

	public void setDefaultDatabasesToNotUndeploy(Set<String> defaultDatabasesToNotUndeploy) {
		this.defaultDatabasesToNotUndeploy = defaultDatabasesToNotUndeploy;
	}
}

/**
 * Defines a set of database plans, including capturing the name of the main content database and whether a test
 * database mirroring that content database should be created.
 */
class DatabasePlans {

	private String mainContentDatabaseName;

	// Using a LinkedHashMap so that plans are first ordered by filename
	private Map<String, DatabasePlan> databasePlanMap = new LinkedHashMap<>();

	private DatabasePlan testDatabasePlan;

	public String getMainContentDatabaseName() {
		return mainContentDatabaseName;
	}

	public void setMainContentDatabaseName(String mainContentDatabaseName) {
		this.mainContentDatabaseName = mainContentDatabaseName;
	}

	public Map<String, DatabasePlan> getDatabasePlanMap() {
		return databasePlanMap;
	}

	public void setDatabasePlanMap(Map<String, DatabasePlan> databasePlanMap) {
		this.databasePlanMap = databasePlanMap;
	}

	public DatabasePlan getTestDatabasePlan() {
		return testDatabasePlan;
	}

	public void setTestDatabasePlan(DatabasePlan testDatabasePlan) {
		this.testDatabasePlan = testDatabasePlan;
	}
}
