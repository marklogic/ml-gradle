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
package com.marklogic.gradle

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.DefaultAppConfigFactory
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandContext
import com.marklogic.appdeployer.command.CommandMapBuilder
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.appdeployer.util.SimplePropertiesSource
import com.marklogic.gradle.task.*
import com.marklogic.gradle.task.admin.InitTask
import com.marklogic.gradle.task.admin.InstallAdminTask
import com.marklogic.gradle.task.alert.DeleteAllAlertConfigsTask
import com.marklogic.gradle.task.alert.DeployAlertingTask
import com.marklogic.gradle.task.client.*
import com.marklogic.gradle.task.cluster.*
import com.marklogic.gradle.task.configuration.DeployConfigurationsTask
import com.marklogic.gradle.task.cpf.DeployCpfTask
import com.marklogic.gradle.task.cpf.LoadDefaultPipelinesTask
import com.marklogic.gradle.task.data.LoadDataTask
import com.marklogic.gradle.task.databases.*
import com.marklogic.gradle.task.datamovement.*
import com.marklogic.gradle.task.es.GenerateModelArtifactsTask
import com.marklogic.gradle.task.export.ExportResourcesTask
import com.marklogic.gradle.task.flexrep.*
import com.marklogic.gradle.task.forests.ConfigureForestReplicasTask
import com.marklogic.gradle.task.forests.DeleteForestReplicasTask
import com.marklogic.gradle.task.forests.DeployCustomForestsTask
import com.marklogic.gradle.task.forests.DeployForestReplicasTask
import com.marklogic.gradle.task.forests.PrintForestPlanTask
import com.marklogic.gradle.task.groups.DeployGroupsTask
import com.marklogic.gradle.task.groups.SetTraceEventsTask
import com.marklogic.gradle.task.hosts.AssignHostsToGroupsTask
import com.marklogic.gradle.task.mimetypes.DeployMimetypesTask
import com.marklogic.gradle.task.mimetypes.UndeployMimetypesTask
import com.marklogic.gradle.task.plugins.InstallPluginsTask
import com.marklogic.gradle.task.plugins.UninstallPluginsTask
import com.marklogic.gradle.task.qconsole.ExportWorkspacesTask
import com.marklogic.gradle.task.qconsole.ImportWorkspacesTask
import com.marklogic.gradle.task.rebalancer.DeployPartitionQueriesTask
import com.marklogic.gradle.task.rebalancer.DeployPartitionsTask
import com.marklogic.gradle.task.rebalancer.TakePartitionOfflineTask
import com.marklogic.gradle.task.rebalancer.TakePartitionOnlineTask
import com.marklogic.gradle.task.restapis.DeployRestApisTask
import com.marklogic.gradle.task.roxy.RoxyMigrateBuildStepsTask
import com.marklogic.gradle.task.roxy.RoxyMigrateFilesTask
import com.marklogic.gradle.task.roxy.RoxyMigratePropertiesTask
import com.marklogic.gradle.task.scaffold.NewAmpTask
import com.marklogic.gradle.task.scaffold.NewDatabaseTask
import com.marklogic.gradle.task.scaffold.NewExternalSecurityTask
import com.marklogic.gradle.task.scaffold.NewGroupTask
import com.marklogic.gradle.task.scaffold.NewPrivilegeTask
import com.marklogic.gradle.task.scaffold.NewProtectedCollectionTask
import com.marklogic.gradle.task.scaffold.NewRoleTask
import com.marklogic.gradle.task.scaffold.NewServerTask
import com.marklogic.gradle.task.scaffold.NewTaskTask
import com.marklogic.gradle.task.scaffold.NewTriggerTask
import com.marklogic.gradle.task.scaffold.NewUserTask
import com.marklogic.gradle.task.test.UnitTestTask
import com.marklogic.gradle.task.test.GenerateUnitTestSuiteTask
import com.marklogic.gradle.task.scaffold.GenerateScaffoldTask
import com.marklogic.gradle.task.schemas.LoadSchemasTask
import com.marklogic.gradle.task.security.*
import com.marklogic.gradle.task.servers.DeployServersTask
import com.marklogic.gradle.task.servers.UndeployOtherServersTask
import com.marklogic.gradle.task.shell.ShellTask
import com.marklogic.gradle.task.tasks.DeleteAllTasksTask
import com.marklogic.gradle.task.tasks.DeployTasksTask
import com.marklogic.gradle.task.tasks.DisableAllTasksTask
import com.marklogic.gradle.task.tasks.EnableAllTasksTask
import com.marklogic.gradle.task.tasks.UndeployTasksTask
import com.marklogic.gradle.task.tasks.WaitForTaskServerTask
import com.marklogic.gradle.task.temporal.DeployTemporalTask
import com.marklogic.gradle.task.trigger.DeployTriggersTask
import com.marklogic.gradle.task.viewschemas.DeployViewSchemasTask
import com.marklogic.mgmt.DefaultManageConfigFactory
import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.ManageConfig
import com.marklogic.mgmt.admin.AdminConfig
import com.marklogic.mgmt.admin.AdminManager
import com.marklogic.mgmt.admin.DefaultAdminConfigFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class MarkLogicPlugin implements Plugin<Project> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass())

	void apply(Project project) {
		logger.info("\nInitializing ml-gradle")

		initializeAppDeployerObjects(project)

		copyGradlePropertiesToCustomTokensIfRequested(project)

		project.getConfigurations().create("mlBundle")
		// Per #420, this is deprecated, but still need to create it
		project.getConfigurations().create("mlRestApi")

		// No group or description on these so they don't show up in "gradle tasks"
		project.task("mlDeployApp", type: DeployAppTask, dependsOn: ["mlDeleteModuleTimestampsFile"])
		project.task("mlUndeployApp", type: UndeployAppTask)

		String deployGroup = "ml-gradle Deploy"
		project.task("mlPostDeploy", group: deployGroup, description: "Add dependsOn to this task to add tasks at the end of mlDeploy").mustRunAfter(["mlDeployApp"])
		project.task("mlPostUndeploy", group: deployGroup, description: "Add dependsOn to this task to add tasks at the end of mlUndeploy").mustRunAfter(["mlUndeployApp"])
		project.task("mlDeploy", group: deployGroup, dependsOn: ["mlDeployApp", "mlPostDeploy"],
			description: "Deploys all application resources in the configuration directory and allows for additional steps via mlPostDeploy.dependsOn. Use -Pignore to specify a comma-delimited list of short class names of ml-app-deployer command classes to ignore while deploying.")
			.mustRunAfter("mlClearModulesDatabase", "mlDeleteResourceTimestampsFile")
		project.task("mlUndeploy", group: deployGroup, dependsOn: ["mlUndeployApp", "mlPostUndeploy", "mlDeleteResourceTimestampsFile"], description: "Undeploys all application resources in the configuration directory and allows for additional steps via mlPostUndeploy.dependsOn; requires -Pconfirm=true to be set so this isn't accidentally executed")
		project.task("mlRedeploy", group: deployGroup, dependsOn: ["mlClearModulesDatabase", "mlDeploy", "mlDeleteResourceTimestampsFile"], description: "Clears the modules database and then deploys the application")
		project.task("mlDeleteResourceTimestampsFile", type: DeleteResourceTimestampsFileTask, group: deployGroup, description: "Delete the properties file in the build directory (stored there by default) that keeps track of when each resource was last deployed; the file path can be overridden by setting the filePath property of this class")
		project.task("mlPreviewDeploy", type: PreviewDeployTask, group: deployGroup, description: "Preview a deployment without making any changes")
		project.task("mlDeployToReplica", type: DeployToReplicaTask, group: deployGroup,
			description: "Deploys application resources in the same manner as mlDeploy, but will not deploy anything that " +
				"involves writing data to a database - such as modules, schemas, and triggers - thus making it safe for use " +
				"when deploying an application to a replica cluster")
		project.task("mlTestConnections", type: TestConnectionsTask, group: deployGroup,
			description: "Test each connection ml-gradle will make to MarkLogic; results of each test will be printed, with " +
				"an exception being thrown if any connection test fails.")

		String adminGroup = "ml-gradle Admin"
		project.task("mlInit", type: InitTask, group: adminGroup, description: "Perform a one-time initialization of a MarkLogic server; uses the properties 'mlLicenseKey' and 'mlLicensee'")
		project.task("mlInstallAdmin", type: InstallAdminTask, group: adminGroup, description: "Perform a one-time installation of an admin user; uses the properties 'mlAdminUsername'/'mlUsername' and 'mlAdminPassword'/'mlPassword'; " +
			"the realm, which defaults to 'public', can optionally be specified on the command line via '-Prealm='")

		String alertGroup = "ml-gradle Alert"
		project.task("mlDeleteAllAlertConfigs", type: DeleteAllAlertConfigsTask, group: alertGroup, description: "Delete all alert configs, which also deletes all of the actions rules associated with them")
		project.task("mlDeployAlerting", type: DeployAlertingTask, group: alertGroup, description: "Deploy each alerting resource - configs, actions, and rules - in the configuration directory")

		String configurationGroup = "ml-gradle Configuration"
		project.task("mlDeployConfigurations", type: DeployConfigurationsTask, group: configurationGroup, description: "Deploy each configuration (requires at least MarkLogic 9.0-5) in the configuration directory")

		String cpfGroup = "ml-gradle CPF"
		project.task("mlDeployCpf", type: DeployCpfTask, group: cpfGroup, description: "Deploy each CPF resource - domains, pipelines, and CPF configs - in the configuration directory").mustRunAfter("mlClearTriggersDatabase")
		project.task("mlRedeployCpf", group: cpfGroup, dependsOn: ["mlClearTriggersDatabase", "mlDeployCpf"], description: "Clears the triggers database and then calls mlDeployCpf; be sure to reload custom triggers after doing this, as they will be deleted as well")
		project.task("mlLoadDefaultPipelines", type: LoadDefaultPipelinesTask, group: cpfGroup, description: "Load default pipelines into a triggers database")

		String clusterGroup = "ml-gradle Cluster"
		project.task("mlAddHost", type: AddHostTask, group: clusterGroup, description: "Add host to the cluster; must define 'host', 'hostGroup' (optional), and 'hostZone' (optional) properties")
		project.task("mlModifyCluster", type: ModifyClusterTask, group: clusterGroup, description: "Modify the properties of the local cluster based on the ml-config/clusters/local-cluster.json file")
		project.task("mlDisableSslFips", type: DisableSslFipsTask, group: clusterGroup, description: "Disable SSL FIPS across the cluster")
		project.task("mlEnableSslFips", type: EnableSslFipsTask, group: clusterGroup, description: "Enable SSL FIPS across the cluster")
		project.task("mlRemoveHost", type: RemoveHostTask, group: clusterGroup, description: "Remove a host from the cluster; must define 'host' property")
		project.task("mlRestartCluster", type: RestartClusterTask, group: clusterGroup, description: "Restart the local cluster")

		String dataGroup = "ml-gradle Data"
		project.task("mlLoadData", type: LoadDataTask, group: dataGroup, dependsOn: "mlPrepareBundles",
			description: "Load files as documents into a content database (or on a DHF project, the final database)")

		String dbGroup = "ml-gradle Database"
		project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: dbGroup, description: "Deletes all documents in the content database; requires -Pconfirm=true to be set so this isn't accidentally executed")
		project.task("mlClearDatabase", type: ClearDatabaseTask, group: dbGroup, description: "Deletes all documents in a database specified by -Pdatabase=(name); requires -Pconfirm=true to be set so this isn't accidentally executed")
		project.task("mlClearModulesDatabase", type: ClearModulesDatabaseTask, group: dbGroup, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")
		project.task("mlClearSchemasDatabase", type: ClearSchemasDatabaseTask, group: dbGroup, description: "Deletes all documents in the schemas database. " +
			"Note that this includes those created via the deployment of resources such as temporal collections and view schemas. You may want to use mlDeleteUserSchemas instead.")
		project.task("mlClearTriggersDatabase", type: ClearTriggersDatabaseTask, group: dbGroup, description: "Deletes all documents in the triggers database")
		project.task("mlDeleteDatabase", type: DeleteDatabaseTask, group: dbGroup, description: "Delete a database along with all of its forests and any replicas; requires -Pconfirm=true to be set so this isn't accidentally executed")
		project.task("mlDeployDatabases", type: DeployDatabasesTask, group: dbGroup, description: "Deploy each database, updating it if it exists, in the configuration directory")
		project.task("mlMergeContentDatabase", type: MergeContentDatabaseTask, group: dbGroup, description: "Merge the database named by mlAppConfig.contentDatabaseName")
		project.task("mlMergeDatabase", type: MergeDatabaseTask, group: dbGroup, description: "Merge the database named by the project property dbName; e.g. gradle mlMergeDatabase -PdbName=my-database")
		project.task("mlReindexContentDatabase", type: ReindexContentDatabaseTask, group: dbGroup, description: "Reindex the database named by mlAppConfig.contentDatabaseName")
		project.task("mlReindexDatabase", type: ReindexDatabaseTask, group: dbGroup, description: "Reindex the database named by the project property dbName; e.g. gradle mlReindexDatabase -PdbName=my-database")
		project.task("mlSetContentUpdatesAllowed", type: SetContentUpdatesAllowedTask, group: dbGroup, description: "Sets updated-allowed on each primary forest for the content database; must set the mode via e.g. -Pmode=flash-backup")
		project.task("mlUpdateIndexes", type: UpdateIndexesTask, group: dbGroup, description: "Update every database by sending a payload that only contains properties related to how data is indexed")

		String dmGroup = "ml-Gradle Data Movement"
		String dmMessage = "Run with -PjobProperties (no value needed) for more information."
		project.task("mlAddCollections", type: AddCollectionsTask, group: dmGroup, description: "Add collections to documents. " + dmMessage)
		project.task("mlAddPermissions", type: AddPermissionsTask, group: dmGroup, description: "Add permissions to documents. " + dmMessage)
		project.task("mlDeleteCollections", type: DeleteCollectionsTask, group: dmGroup, description: "Delete collections. " + dmMessage)
		project.task("mlExportBatchesToDirectory", type: ExportBatchesToDirectoryTask, group: dmGroup, description: "Export batches of documents to files in a directory. " + dmMessage)
		project.task("mlExportBatchesToZips", type: ExportBatchesToZipsTask, group: dmGroup, description: "Export batches of documents to zips in a directory. " + dmMessage)
		project.task("mlExportToFile", type: ExportToFileTask, group: dmGroup, description: "Export documents to a single file. " + dmMessage)
		project.task("mlExportToZip", type: ExportToZipTask, group: dmGroup, description: "Export documents to a single zip. " + dmMessage)
		project.task("mlRemoveCollections", type: RemoveCollectionsTask, group: dmGroup, description: "Remove collections from documents. " + dmMessage)
		project.task("mlRemovePermissions", type: RemovePermissionsTask, group: dmGroup, description: "Remove permissions from documents. " + dmMessage)
		project.task("mlSetCollections", type: SetCollectionsTask, group: dmGroup, description: "Set collections on documents. " + dmMessage)
		project.task("mlSetPermissions", type: SetPermissionsTask, group: dmGroup, description: "Set permissions on documents. " + dmMessage)

		String devGroup = "ml-gradle Development"
		final String newResourceMessage = "Non-complex properties can be specified via -Pml-(name of property)."
		project.task("mlCreateResource", type: CreateResourceTask, group: devGroup, description: "Create a new resource extension in the modules services directory; use -PresourceName and -PresourceType to set the resource name and type (either xqy or sjs)")
		project.task("mlCreateTransform", type: CreateTransformTask, group: devGroup, description: "Create a new transform in the modules transforms directory; use -PtransformName and -PtransformType to set the transform name and type (xqy, xsl, or sjs)")
		project.task("mlExportResources", type: ExportResourcesTask, group: devGroup, description: "Export resources based on a properties file specified via -PpropertiesFile, -Pprefix, or -Pregex; use -PincludeTypes to select resource types to export via a comma-delimited string; use -PexportPath to specify where to export resources to")
		project.task("mlPrepareBundles", type: PrepareBundlesTask, group: devGroup, dependsOn: project.configurations["mlBundle"], description: "Downloads (if necessary) and unzips in the build directory all mlBundle dependencies")
		project.task("mlPrepareRestApiDependencies", type: PrepareBundlesTask, group: devGroup, dependsOn: project.configurations["mlBundle"], description: "Deprecated in 3.13.0; please use mlPrepareBundles instead")
		project.task("mlPrintCommands", type: PrintCommandsTask, group: devGroup, description: "Print information about each command used by mlDeploy and mlUndeploy")
		project.task("mlPrintProperties", type: PrintPropertiesTask, group: devGroup, description: "Print all of the properties supported by ml-gradle")
		project.task("mlPrintTokens", type: PrintTokensTask, group: devGroup, description: "Print the customTokens map on the mlAppConfig object (typically for debugging purposes)")
		project.task("mlNewProject", type: NewProjectTask, group: devGroup, description: "Run a wizard for creating a new project, which includes running mlScaffold")
		project.task("mlNewAmp", type: NewAmpTask, group: devGroup, description: "Generate a new amp resource file. " + newResourceMessage)
		project.task("mlNewDatabase", type: NewDatabaseTask, group: devGroup, description: "Generate a new database resource file. " + newResourceMessage)
		project.task("mlNewExternalSecurity", type: NewExternalSecurityTask, group: devGroup, description: "Generate a new external security resource file. " + newResourceMessage)
		project.task("mlNewGroup", type: NewGroupTask, group: devGroup, description: "Generate a new group resource file. " + newResourceMessage)
		project.task("mlNewPrivilege", type: NewPrivilegeTask, group: devGroup, description: "Generate a new privilege resource file. " + newResourceMessage)
		project.task("mlNewProtectedCollection", type: NewProtectedCollectionTask, group: devGroup, description: "Generate a new protected collection resource file. " + newResourceMessage)
		project.task("mlNewRole", type: NewRoleTask, group: devGroup, description: "Generate a new role resource file. " + newResourceMessage)
		project.task("mlNewServer", type: NewServerTask, group: devGroup, description: "Generate a new server resource file. " + newResourceMessage)
		project.task("mlNewTask", type: NewTaskTask, group: devGroup, description: "Generate a new task resource file. " + newResourceMessage)
		project.task("mlNewTrigger", type: NewTriggerTask, group: devGroup, description: "Generate a new trigger resource file. A triggers database name must be specified via -Pdatabase=(database name). " + newResourceMessage)
		project.task("mlNewUser", type: NewUserTask, group: devGroup, description: "Generate a new user resource file. " + newResourceMessage)
		project.task("mlScaffold", type: GenerateScaffoldTask, group: devGroup, description: "Generate project scaffold for a new project")

		String hostsGroup = "ml-gradle Host"
		project.task("mlAssignHostsToGroups", type: AssignHostsToGroupsTask, group: hostsGroup, description: "Assign each specified host to its corresponding group, as defined by the mlHostGroups property")

		String esGroup = "ml-gradle Entity Services"
		project.task("mlGenerateModelArtifacts", type: GenerateModelArtifactsTask, group: esGroup, description: "Generate model artifacts for the Entity Services models in the default directory of ./data/entity-services")

		String flexrepGroup = "ml-gradle Flexible Replication"
		project.task("mlDeleteAllFlexrepConfigs", type: DeleteAllFlexrepConfigsTask, group: flexrepGroup, description: "Delete all Flexrep configs and their associated targets")
		project.task("mlDeployFlexrep", type: DeployFlexrepTask, group: flexrepGroup, description: "Deploy Flexrep configs and targets in the configuration directory")
		project.task("mlDeployFlexrepAtPath", type: DeployFlexrepAtPathTask, group: flexrepGroup, description: "Deploy all Flexrep resources in a directory under ml-config/flexrep with a name matching the property mlFlexrepPath")
		project.task("mlDisableAllFlexrepTargets", type: DisableAllFlexrepTargetsTask, group: flexrepGroup, description: "Disable every target on every flexrep config")
		project.task("mlEnableAllFlexrepTargets", type: EnableAllFlexrepTargetsTask, group: flexrepGroup, description: "Enable every target on every flexrep config")

		String forestGroup = "ml-gradle Forest"
		project.task("mlConfigureForestReplicas", type: ConfigureForestReplicasTask, group: forestGroup, description: "Deprecated - configure forest replicas via the command.forestNamesAndReplicaCounts map")
		project.task("mlDeleteForestReplicas", type: DeleteForestReplicasTask, group: forestGroup, description: "Deprecated - delete forest replicas via the command.forestNamesAndReplicaCounts map; requires -Pconfirm=true to be set so this isn't accidentally executed")
		project.task("mlDeployCustomForests", type: DeployCustomForestsTask, group: forestGroup, description: "Deploy custom forests as defined in subdirectories of the forests configuration directory")
		project.task("mlDeployForestReplicas", type: DeployForestReplicasTask, group: forestGroup, description: "Prefer this over mlConfigureForestReplicas; it does the same thing, but uses the ConfigureForestReplicasCommand that is used by mlDeploy")
		project.task("mlPrintForestPlan", type: PrintForestPlanTask, group: forestGroup, description: "Print a list of primary forests to be created for a database specified by -Pdatabase=(name of database) when the database is next deployed. " +
			"This is only intended to be used when forests are created dynamically via properties.")

		String groupsGroup = "ml-gradle Group"
		project.task("mlDeployGroups", type: DeployGroupsTask, group: groupsGroup, description: "Deploy each group, updating it if it exists, in the configuration directory")
		project.task("mlSetTraceEvents", type: SetTraceEventsTask, group: groupsGroup, description: "Set trace events via a comma-delimited string - e.g. -Pevents=event1,event2")

		String mimetypesGroup = "ml-gradle Mimetypes"
		project.task("mlDeployMimetypes", type: DeployMimetypesTask, group: mimetypesGroup, description: "Deploy each mimetype, updating it if it exists, in the configuration directory")
		project.task("mlUndeployMimetypes", type: UndeployMimetypesTask, group: mimetypesGroup, description: "Undeploy each mimetype defined in the configuration directory")

		String modulesGroup = "ml-gradle Modules"
		project.task("mlLoadModules", type: LoadModulesTask, group: modulesGroup, dependsOn: ["mlPrepareBundles", "mlDeleteModuleTimestampsFile"], description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter(["mlClearModulesDatabase"])
		project.task("mlReloadModules", group: modulesGroup, dependsOn: ["mlClearModulesDatabase", "mlLoadModules"], description: "Reloads modules by first clearing the modules database and then loading modules")
		project.task("mlWatch", type: WatchTask, group: modulesGroup, description: "Run a loop that checks for new/modified modules every second and loads any that it finds. To ignore files that are already dirty and only process new changes, include -PignoreDirty=true . ")
		project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: modulesGroup, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded")
		project.task("mlExportModules", type: ExportModulesTask, group: modulesGroup, description: "Export modules matching a URI pattern of ** (can be overridden via -PuriPattern) from the database " +
			"defined by mlModulesDatabaseName (can be overridden via -PdatabaseName) to the last path defined by mlModulePaths (can be overridden via -PexportPath). For each module that cannot be exported, " +
			"an error will be logged; an error will be thrown instead by setting -PlogErrors to false.")

		String pluginsGroup = "ml-gradle Plugin"
		project.task("mlInstallPlugins", type: InstallPluginsTask, group: pluginsGroup, dependsOn: "mlPrepareBundles",
			description: "Makes, inserts, and installs system plugins defined in the project and by mlBundle dependencies")
		project.task("mlUninstallPlugins", type: UninstallPluginsTask, group: pluginsGroup, dependsOn: "mlPrepareBundles",
			description: "Makes and uninstalls system plugins defined in the project and by mlBundle dependencies")

		String qconsoleGroup = "ml-gradle qconsole"
		project.task("mlImportWorkspaces", type: ImportWorkspacesTask, group: qconsoleGroup, description: "Import workspaces into qconsole")
		project.task("mlExportWorkspaces", type: ExportWorkspacesTask, group: qconsoleGroup, description: "Export workspaces from qconsole")

		String rebalancerGroup = "ml-gradle Rebalancer"
		project.task("mlDeployPartitions", type: DeployPartitionsTask, group: rebalancerGroup, description: "Deploy database-specific partitions")
		project.task("mlDeployPartitionQueries", type: DeployPartitionQueriesTask, group: rebalancerGroup, description: "Deploy database-specific partition queries")
		project.task("mlTakePartitionOffline", type: TakePartitionOfflineTask, group: rebalancerGroup,
			description: "Take a partition offline. Use -Pdatabase=dbName and -Ppartition=partitionName to specify the database and partition names.")
		project.task("mlTakePartitionOnline", type: TakePartitionOnlineTask, group: rebalancerGroup,
			description: "Take a partition online. Use -Pdatabase=dbName and -Ppartition=partitionName to specify the database and partition names.")

		String restApisGroup = "ml-gradle REST API"
		project.task("mlDeployRestApis", type: DeployRestApisTask, group: restApisGroup, description: "Deploy the REST API instances defined by a resource file or the mlRestPort/mlTestRestPort properties")

		String schemasGroup = "ml-gradle Schemas"
		project.task("mlDeleteUserSchemas", type: DeleteUserSchemasTask, group: schemasGroup, description: "Delete documents in a schemas database that were not created via the deployment of resources such as temporal collections or view schemas")
		project.task("mlLoadSchemas", type: LoadSchemasTask, group: schemasGroup, dependsOn: "mlPrepareBundles",
			description: "Loads files into schema databases, including schema files that are part of mlBundle dependencies").mustRunAfter("mlDeleteUserSchemas")
		project.task("mlReloadSchemas", dependsOn: ["mlDeleteUserSchemas", "mlLoadSchemas"], group: schemasGroup, description: "Deletes user schemas via mlDeleteUserSchemas and then loads schemas via mlLoadSchemas")

		String serverGroup = "ml-gradle Server"
		project.task("mlDeployServers", type: DeployServersTask, group: serverGroup, description: "Updates the REST API server (if it exists) and deploys each other server, updating it if it exists, in the configuration directory ")
		project.task("mlUndeployOtherServers", type: UndeployOtherServersTask, group: serverGroup, description: "Delete any non-REST API servers (e.g. ODBC and XBC servers) defined by server files in the configuration directory")

		String securityGroup = "ml-gradle Security"
		project.task("mlDeployAmps", type: DeployAmpsTask, group: securityGroup, description: "Deploy each amp, updating it if it exists, in the configuration directory")
		project.task("mlDeployCertificateAuthorities", type: DeployCertificateAuthoritiesTask, group: securityGroup, description: "Deploy each certificate authority, updating it if it exists, in the configuration directory")
		project.task("mlDeployCertificateTemplates", type: DeployCertificateTemplatesTask, group: securityGroup, description: "Deploy each certificate template, updating it if it exists, in the configuration directory")
		project.task("mlDeployExternalSecurity", type: DeployExternalSecurityTask, group: securityGroup, description: "Deploy external security configurations, updating each if it exists, in the configuration directory")
		project.task("mlDeployHostCertificateTemplates", type: DeployHostCertificateTemplatesTask, group: securityGroup, description: "Deploy host certificate templates, updating each if it exists, in the configuration directory")
		project.task("mlDeploySecureCredentials", type: DeploySecureCredentialsTask, group: securityGroup, description: "Deploy secure credentials configurations, updating each if it exists, in the configuration directory")
		project.task("mlDeployPrivileges", type: DeployPrivilegesTask, group: securityGroup, description: "Deploy each privilege, updating it if it exists, in the configuration directory")
		project.task("mlDeployProtectedCollections", type: DeployProtectedCollectionsTask, group: securityGroup, description: "Deploy each protected collection, updating it if it exists, in the configuration directory")
		project.task("mlDeployProtectedPaths", type: DeployProtectedPathsTask, group: securityGroup, description: "Deploy each protected path, updating it if it exists, in the configuration directory")
		project.task("mlDeployQueryRolesets", type: DeployQueryRolesetsTask, group: securityGroup, description: "Deploy each query roleset, updating it if it exists, in the configuration directory")
		project.task("mlDeployRoles", type: DeployRolesTask, group: securityGroup, description: "Deploy each role, updating it if it exists, in the configuration directory")
		project.task("mlDeploySecurity", type: DeploySecurityTask, group: securityGroup, description: "Deploy each security resource, updating it if it exists, in the configuration directory")
		project.task("mlDeployUsers", type: DeployUsersTask, group: securityGroup, description: "Deploy each user, updating it if it exists, in the configuration directory")
		project.task("mlUndeployAmps", type: UndeployAmpsTask, group: securityGroup, description: "Undeploy (delete) each amp in the configuration directory")
		project.task("mlUndeployCertificateTemplates", type: UndeployCertificateTemplatesTask, group: securityGroup, description: "Undeploy (delete) each certificate template in the configuration directory")
		project.task("mlUndeployExternalSecurity", type: UndeployExternalSecurityTask, group: securityGroup, description: "Undeploy (delete) each external security configuration in the configuration directory")
		project.task("mlUndeploySecureCredentials", type: UndeploySecureCredentialsTask, group: securityGroup, description: "Undeploy (delete) each secure credentials configuration in the configuration directory")
		project.task("mlUndeployPrivileges", type: UndeployPrivilegesTask, group: securityGroup, description: "Undeploy (delete) each privilege in the configuration directory")
		project.task("mlUndeployProtectedCollections", type: UndeployProtectedCollectionsTask, group: securityGroup, description: "Undeploy (delete) each protected collection in the configuration directory")
		project.task("mlUndeployProtectedPaths", type: UndeployProtectedPathsTask, group: securityGroup, description: "Undeploy (delete) each protected path in the configuration directory")
		project.task("mlUndeployQueryRolesets", type: UndeployQueryRolesetsTask, group: securityGroup, description: "Undeploy (delete) each query roleset in the configuration directory")
		project.task("mlUndeployRoles", type: UndeployRolesTask, group: securityGroup, description: "Undeploy (delete) each role in the configuration directory")
		project.task("mlUndeployUsers", type: UndeployUsersTask, group: securityGroup, description: "Undeploy (delete) each user in the configuration directory")
		project.task("mlUndeploySecurity", type: UndeploySecurityTask, group: securityGroup, description: "Undeploy (delete) all security resources in the configuration directory")

		String sqlGroup = "ml-gradle SQL"
		project.task("mlDeployViewSchemas", type: DeployViewSchemasTask, group: sqlGroup, description: "Deploy each SQL view schema, updating it if it exists, in the configuration directory")

		String taskGroup = "ml-gradle Task"
		project.task("mlDeleteAllTasks", type: DeleteAllTasksTask, group: taskGroup, description: "Delete all scheduled tasks in the cluster")
		project.task("mlDeployTasks", type: DeployTasksTask, group: taskGroup, description: "Deploy each scheduled task, updating it if it exists, in the configuration directory; also updates the task server if a task server config file exists")
		project.task("mlDisableAllTasks", type: DisableAllTasksTask, group: taskGroup, description: "Disable each scheduled task in the group identified by the mlGroupName property, which defaults to 'Default'")
		project.task("mlEnableAllTasks", type: EnableAllTasksTask, group: taskGroup, description: "Enable each scheduled task in the group identified by the mlGroupName property, which defaults to 'Default'")
		project.task("mlUndeployTasks", type: UndeployTasksTask, group: taskGroup, description: "Undeploy (delete) each scheduled task in the configuration directory")
		project.task("mlWaitForTaskServer", type: WaitForTaskServerTask, group: taskGroup, description: "Wait for the task server to not have any requests in progress")

		String temporalGroup = "ml-gradle Temporal"
		project.task("mlDeployTemporal", type: DeployTemporalTask, group: temporalGroup, description: "Deploy temporal configuration. Note that (currently) you can't update the temporal configuration (collection properties and LSQT properties excepted)")

		String triggerGroup = "ml-gradle Trigger"
		project.task("mlDeployTriggers", type: DeployTriggersTask, group: triggerGroup, description: "Deploy each trigger, updating it if it exists, in the configuration directory")

		String shellGroup = "ml-gradle Shell"
		project.task("mlShell", type: ShellTask, group: shellGroup, description: "Run groovysh with MarkLogic-specific support built in")

		String roxyGroup = "ml-gradle Roxy";
		project.task("mlRoxyMigrateBuildSteps", type: RoxyMigrateBuildStepsTask, group: roxyGroup, description: "Migrate build steps from deploy/app_specific.rb into custom Gradle tasks. " +
			"Use -ProxyProjectPath to define the location of your Roxy project, and -PappSpecificPath to define a path other than deploy/app_specific.rb")
		project.task("mlRoxyMigrateFiles", type: RoxyMigrateFilesTask, group: roxyGroup, description: "Migrate Roxy source files into this Gradle project. " +
			"Use -ProxyProjectPath to define the location of your Roxy project.")
		project.task("mlRoxyMigrateProperties", type: RoxyMigratePropertiesTask, group: roxyGroup, description: "Migrate Roxy properties into the gradle.properties file in this project. " +
			"Use -ProxyProjectPath to define the location of your Roxy project.")
		project.task("mlRoxyMigrateProject", group: roxyGroup, description: "Run all tasks for migrating a Roxy project into this Gradle project. " +
			"Use -ProxyProjectPath to define the location of your Roxy project.", dependsOn: ["mlRoxyMigrateBuildSteps", "mlRoxyMigrateFiles", "mlRoxyMigrateProperties"])

		String unitTestGroup = "ml-gradle Unit Test"
		project.task("mlGenerateUnitTestSuite", type: GenerateUnitTestSuiteTask, group: unitTestGroup,
			description: "Generate a marklogic-unit-test test suite. The test suite files are written to src/test/ml-modules/root/test/suites by default; use -PsuitesPath to override this. " +
				"Can use -PsuiteName to override the name of the test suite, -PtestName to override the name of the test module, and -Planguage to specify \"sjs\" or \"xqy\" test code.")
		project.task("mlUnitTest", type: UnitTestTask, group: unitTestGroup, description: "Run tests found under /test/suites in the modules database. " +
			"Connects to MarkLogic via the REST API server defined by mlTestRestPort (or by mlRestPort if mlTestRestPort is not set), and uses mlRest* properties for authentication. " +
			"Use -Psuites to specify one or more suite names, separated by commas, to run. If not set, all suites will be run. " +
			"Use -Ptests to specify one or more test names, separated by commas, to run. If not set, all tests in a suite will be run. " +
			"Use -PunitTestResultsPath to override where test result files are written, which defaults to build/test-results/marklogic-unit-test. " +
			"Use -PrunCodeCoverage to enable code coverage support when running the tests. " +
			"Use -PrunTeardown and -PrunSuiteTeardown to control whether teardown and suite teardown scripts are run; these default to 'true' and can be set to 'false' instead. ")

		// Any granular task that deploys/undeploys resources may need to do so for a resource in a bundle, so these
		// tasks must all depend on mlPrepareBundles
		project.tasks.each { task ->
			if (task.name.startsWith("mlDeploy") || task.name.startsWith("mlUndeploy")) {
				task.dependsOn("mlPrepareBundles")
			}
		}

		logger.info("Finished initializing ml-gradle\n")
	}

	/**
	 * New in 3.2.0 - if mlPropsAsTokens is set to true, then all Gradle properties will be added to the AppConfig
	 * customTokens map with "%%" as a default prefix and suffix. The prefix and suffix can be overridden via
	 * mlTokenPrefix and mlTokenSuffix respectively.
	 */
	void copyGradlePropertiesToCustomTokensIfRequested(Project project) {
		boolean usePropsAsTokens = true
		if (project.hasProperty("mlPropsAsTokens")) {
			usePropsAsTokens = !project.property("mlPropsAsTokens").equals("false")
		}
		if (usePropsAsTokens) {
			AppConfig appConfig = project.extensions.getByName("mlAppConfig")
			Properties props = new Properties()
			Map<String, ?> gradleProperties = project.getProperties()
			for (String key : gradleProperties.keySet()) {
				if ("properties".equals(key)) {
					continue
				}
				Object val = gradleProperties.get(key)
				if (val instanceof String) {
					props.setProperty(key, val)
				} else {
					props.setProperty(key, val.toString())
				}
			}

			String prefix = "%%"
			String suffix = "%%"
			if (project.hasProperty("mlTokenPrefix")) {
				prefix = project.property("mlTokenPrefix")
			}
			if (project.hasProperty("mlTokenSuffix")) {
				suffix = project.property("mlTokenSuffix")
			}

			appConfig.populateCustomTokens(new SimplePropertiesSource(props), prefix, suffix)
		}
	}

	void initializeAppDeployerObjects(Project project) {
		DefaultAdminConfigFactory adminConfigFactory = new DefaultAdminConfigFactory(new ProjectPropertySource(project))
		project.extensions.add("mlAdminConfigFactory", adminConfigFactory)
		AdminConfig adminConfig = adminConfigFactory.newAdminConfig()
		project.extensions.add("mlAdminConfig", adminConfig)

		ProjectPropertySource propertySource = new ProjectPropertySource(project);

		DefaultAppConfigFactory appConfigFactory = new DefaultAppConfigFactory(propertySource)
		// The ConfigDir objects constructed by AppConfig must all be relative to the project directory
		// when using Java 11. In case this causes problems, a user can disable this via the below property
		if (project.hasProperty("mlIgnoreProjectDir") && "true".equals(project.property("mlIgnoreProjectDir"))) {
			println "The Gradle projectDir will not be used to resolve file paths"
		} else {
			appConfigFactory.setProjectDir(project.getProjectDir())
		}
		project.extensions.add("mlAppConfigFactory", appConfigFactory)

		AppConfig appConfig = appConfigFactory.newAppConfig()
		project.extensions.add("mlAppConfig", appConfig)

		DefaultManageConfigFactory manageConfigFactory = new DefaultManageConfigFactory(new ProjectPropertySource(project))
		project.extensions.add("mlManageConfigFactory", manageConfigFactory)
		ManageConfig manageConfig = manageConfigFactory.newManageConfig()
		project.extensions.add("mlManageConfig", manageConfig)

		ManageClient manageClient = new ManageClient(manageConfig)
		project.extensions.add("mlManageClient", manageClient)

		AdminManager adminManager = new AdminManager(adminConfig)
		project.extensions.add("mlAdminManager", adminManager)

		CommandContext context = new CommandContext(appConfig, manageClient, adminManager)
		project.extensions.add("mlCommandContext", context)

		project.extensions.add("mlAppDeployer", newAppDeployer(project, context))
	}

	/**
	 * Creates an AppDeployer with a default set of commands. A developer can then modify this in an
	 * ext block.
	 */
	AppDeployer newAppDeployer(Project project, CommandContext context) {
		Map<String, List<Command>> commandMap = new CommandMapBuilder().buildCommandMap();
		List<Command> commands = new ArrayList<>();
		for (String name : commandMap.keySet()) {
			project.extensions.add(name, commandMap.get(name));
			commands.addAll(commandMap.get(name));
		}
		SimpleAppDeployer deployer = new SimpleAppDeployer(context.getManageClient(), context.getAdminManager())
		deployer.setCommands(commands)
		return deployer
	}
}
