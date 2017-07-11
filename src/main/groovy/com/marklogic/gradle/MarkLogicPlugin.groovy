package com.marklogic.gradle

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.DefaultAppConfigFactory
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandContext
import com.marklogic.appdeployer.command.CommandMapBuilder
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.DeleteModuleTimestampsFileTask
import com.marklogic.gradle.task.DeployAppTask
import com.marklogic.gradle.task.NewProjectTask
import com.marklogic.gradle.task.PrintCommandsTask
import com.marklogic.gradle.task.UndeployAppTask
import com.marklogic.gradle.task.admin.InitTask
import com.marklogic.gradle.task.admin.InstallAdminTask
import com.marklogic.gradle.task.alert.DeleteAllAlertConfigsTask
import com.marklogic.gradle.task.alert.DeployAlertingTask
import com.marklogic.gradle.task.client.*
import com.marklogic.gradle.task.cluster.*
import com.marklogic.gradle.task.cpf.DeployCpfTask
import com.marklogic.gradle.task.cpf.LoadDefaultPipelinesTask
import com.marklogic.gradle.task.databases.*
import com.marklogic.gradle.task.es.GenerateModelArtifactsTask
import com.marklogic.gradle.task.export.ExportResourcesTask
import com.marklogic.gradle.task.flexrep.*
import com.marklogic.gradle.task.forests.ConfigureForestReplicasTask
import com.marklogic.gradle.task.forests.DeleteForestReplicasTask
import com.marklogic.gradle.task.forests.DeployCustomForestsTask
import com.marklogic.gradle.task.forests.DeployForestReplicasTask
import com.marklogic.gradle.task.groups.DeployGroupsTask
import com.marklogic.gradle.task.groups.SetTraceEventsTask
import com.marklogic.gradle.task.mimetypes.DeployMimetypesTask
import com.marklogic.gradle.task.qconsole.ExportWorkspacesTask
import com.marklogic.gradle.task.qconsole.ImportWorkspacesTask
import com.marklogic.gradle.task.roxy.RoxyCopyFilesTask
import com.marklogic.gradle.task.roxy.RoxyCopyPropertiesTask
import com.marklogic.gradle.task.scaffold.GenerateScaffoldTask
import com.marklogic.gradle.task.schemas.LoadSchemasTask
import com.marklogic.gradle.task.security.*
import com.marklogic.gradle.task.servers.DeployServersTask
import com.marklogic.gradle.task.servers.UndeployOtherServersTask
import com.marklogic.gradle.task.shell.ShellTask
import com.marklogic.gradle.task.tasks.DeleteAllTasksTask
import com.marklogic.gradle.task.tasks.DeployTasksTask
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
import com.sun.jersey.core.spi.component.ProviderServices
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

import java.util.logging.Level
import java.util.logging.Logger

class MarkLogicPlugin implements Plugin<Project> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass())

	void apply(Project project) {
		logger.info("\nInitializing ml-gradle")

		quietDownJerseyLogging()

		initializeAppDeployerObjects(project)

		project.getConfigurations().create("mlRestApi")

		// No group or description on these so they don't show up in "gradle tasks"
		project.task("mlDeployApp", type: DeployAppTask, dependsOn: ["mlDeleteModuleTimestampsFile", "mlPrepareRestApiDependencies"])
		project.task("mlUndeployApp", type: UndeployAppTask)

		String deployGroup = "ml-gradle Deploy"
		project.task("mlPostDeploy", group: deployGroup, description: "Add dependsOn to this task to add tasks at the end of mlDeploy").mustRunAfter(["mlDeployApp"])
		project.task("mlPostUndeploy", group: deployGroup, description: "Add dependsOn to this task to add tasks at the end of mlUndeploy").mustRunAfter(["mlUndeployApp"])
		project.task("mlDeploy", group: deployGroup, dependsOn: ["mlDeployApp", "mlPostDeploy"], description: "Deploys all application resources in the configuration directory and allows for additional steps via mlPostDeploy.dependsOn").mustRunAfter("mlClearModulesDatabase")
		project.task("mlUndeploy", group: deployGroup, dependsOn: ["mlUndeployApp", "mlPostUndeploy"], description: "Undeploys all application resources in the configuration directory and allows for additional steps via mlPostUndeploy.dependsOn")
		project.task("mlRedeploy", group: deployGroup, dependsOn: ["mlClearModulesDatabase", "mlDeploy"], description: "Clears the modules database and then deploys the application")

		String adminGroup = "ml-gradle Admin"
		project.task("mlInit", type: InitTask, group: adminGroup, description: "Perform a one-time initialization of a MarkLogic server; uses the properties 'mlLicenseKey' and 'mlLicensee'")
		project.task("mlInstallAdmin", type: InstallAdminTask, group: adminGroup, description: "Perform a one-time installation of an admin user; uses the properties 'mlAdminUsername'/'mlUsername' and 'mlAdminPassword'/'mlPassword'")

		String alertGroup = "ml-gradle Alert"
		project.task("mlDeleteAllAlertConfigs", type: DeleteAllAlertConfigsTask, group: alertGroup, description: "Delete all alert configs, which also deletes all of the actions rules associated with them")
		project.task("mlDeployAlerting", type: DeployAlertingTask, group: alertGroup, description: "Deploy each alerting resource - configs, actions, and rules - in the configuration directory")

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

		String dbGroup = "ml-gradle Database"
		project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: dbGroup, description: "Deletes all documents in the content database; requires -PdeleteAll=true to be set so you don't accidentally do this")
		project.task("mlClearModulesDatabase", type: ClearModulesDatabaseTask, group: dbGroup, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")
		project.task("mlClearSchemasDatabase", type: ClearSchemasDatabaseTask, group: dbGroup, description: "Deletes all documents in the schemas database")
		project.task("mlClearTriggersDatabase", type: ClearTriggersDatabaseTask, group: dbGroup, description: "Deletes all documents in the triggers database")
		project.task("mlDeleteCollection", type: DeleteCollectionTask, group: dbGroup, description: "Delete the collection of documents in the content database; use -Pcollection=name to specify the collection name on the command line")
		project.task("mlDeployDatabases", type: DeployDatabasesTask, group: dbGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Deploy each database, updating it if it exists, in the configuration directory")
		project.task("mlMergeContentDatabase", type: MergeContentDatabaseTask, group: dbGroup, description: "Merge the database named by mlAppConfig.contentDatabaseName")
		project.task("mlMergeDatabase", type: MergeDatabaseTask, group: dbGroup, description: "Merge the database named by the project property dbName; e.g. gradle mlMergeDatabase -PdbName=my-database")
		project.task("mlReindexContentDatabase", type: ReindexContentDatabaseTask, group: dbGroup, description: "Reindex the database named by mlAppConfig.contentDatabaseName")
		project.task("mlReindexDatabase", type: ReindexDatabaseTask, group: dbGroup, description: "Reindex the database named by the project property dbName; e.g. gradle mlReindexDatabase -PdbName=my-database")
		project.task("mlSetContentUpdatesAllowed", type: SetContentUpdatesAllowedTask, group: dbGroup, description: "Sets updated-allowed on each primary forest for the content database; must set the mode via e.g. -Pmode=flash-backup")

		String devGroup = "ml-gradle Development"
		project.task("mlCreateResource", type: CreateResourceTask, group: devGroup, description: "Create a new resource extension in the modules services directory; use -PresourceName and -PresourceType to set the resource name and type (either xqy or sjs)")
		project.task("mlCreateTransform", type: CreateTransformTask, group: devGroup, description: "Create a new transform in the modules transforms directory; use -PtranssformName and -PtransformType to set the transform name and type (xqy, xsl, or sjs)")
		project.task("mlExportResources", type: ExportResourcesTask, group: devGroup, description: "Export resources based on a properties file specified via -PpropertiesFile, -Pprefix, or -Pregex; use -PincludeTypes to select resource types to export via a comma-delimited string; use -PexportPath to specify where to export resources to")
		project.task("mlPrepareRestApiDependencies", type: PrepareRestApiDependenciesTask, group: devGroup, dependsOn: project.configurations["mlRestApi"], description: "Downloads (if necessary) and unzips in the build directory all mlRestApi dependencies")
		project.task("mlNewProject", type: NewProjectTask, group: devGroup, description: "Run a wizard for creating a new project, which includes running mlScaffold")
		project.task("mlScaffold", type: GenerateScaffoldTask, group: devGroup, description: "Generate project scaffold for a new project")

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
		project.task("mlDeleteForestReplicas", type: DeleteForestReplicasTask, group: forestGroup, description: "Deprecated - delete forest replicas via the command.forestNamesAndReplicaCounts map")
		project.task("mlDeployCustomForests", type: DeployCustomForestsTask, group: forestGroup, description: "Deploy custom forests as defined in subdirectories of the forests configuration directory")
		project.task("mlDeployForestReplicas", type: DeployForestReplicasTask, group: forestGroup, description: "Prefer this over mlConfigureForestReplicas; it does the same thing, but uses the ConfigureForestReplicasCommand that is used by mlDeploy")

		String groupsGroup = "ml-gradle Group"
		project.task("mlDeployGroups", type: DeployGroupsTask, group: groupsGroup, description: "Deploy each group, updating it if it exists, in the configuration directory")
		project.task("mlSetTraceEvents", type: SetTraceEventsTask, group: groupsGroup, description: "Set trace events via a comma-delimited string - e.g. -Pevents=event1,event2")

		String mimetypesGroup = "ml-gradle Mimetypes"
		project.task("mlDeployMimetypes", type: DeployMimetypesTask, group: mimetypesGroup, description: "Deploy each mimetype, updating it if it exists, in the configuration directory")

		String modulesGroup = "ml-gradle Modules"
		project.task("mlLoadModules", type: LoadModulesTask, group: modulesGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter(["mlClearModulesDatabase"])
		project.task("mlReloadModules", group: modulesGroup, dependsOn: ["mlClearModulesDatabase", "mlLoadModules"], description: "Reloads modules by first clearing the modules database and then loading modules")
		project.task("mlWatch", type: WatchTask, group: modulesGroup, description: "Run a loop that checks for new/modified modules every second and loads any that it finds")
		project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: modulesGroup, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded")

		String qconsoleGroup = "ml-gradle qconsole"
		project.task("mlImportWorkspaces", type: ImportWorkspacesTask, group: qconsoleGroup, description: "Import workspaces into qconsole")
		project.task("mlExportWorkspaces", type: ExportWorkspacesTask, group: qconsoleGroup, description: "Export workspaces from qconsole")

		String schemasGroup = "ml-gradle Schemas"
		project.task("mlLoadSchemas", type: LoadSchemasTask, group: schemasGroup, description: "Loads special-purpose data into the schemas database (XSD schemas, Inference rules, and [MarkLogic 9] Extraction Templates)").mustRunAfter("mlClearSchemasDatabase")
		project.task("mlReloadSchemas", dependsOn: ["mlClearSchemasDatabase", "mlLoadSchemas"], group: schemasGroup, description: "Clears schemas database then loads special-purpose data into the schemas database (XSD schemas, Inference rules, and [MarkLogic 9] Extraction Templates)")

		String serverGroup = "ml-gradle Server"
		project.task("mlDeployServers", type: DeployServersTask, group: serverGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the REST API server (if it exists) and deploys each other server, updating it if it exists, in the configuration directory ")
		project.task("mlUndeployOtherServers", type: UndeployOtherServersTask, group: serverGroup, description: "Delete any non-REST API servers (e.g. ODBC and XBC servers) defined by server files in the configuration directory")

		String securityGroup = "ml-gradle Security"
		project.task("mlDeployAmps", type: DeployAmpsTask, group: securityGroup, description: "Deploy each amp, updating it if it exists, in the configuration directory")
		project.task("mlDeployCertificateAuthorities", type: DeployCertificateAuthoritiesTask, group: securityGroup, description: "Deploy each certificate authority, updating it if it exists, in the configuration directory")
		project.task("mlDeployCertificateTemplates", type: DeployCertificateTemplatesTask, group: securityGroup, description: "Deploy each certificate template, updating it if it exists, in the configuration directory")
		project.task("mlDeployExternalSecurity", type: DeployExternalSecurityTask, group: securityGroup, description: "Deploy external security configurations, updating each if it exists, in the configuration directory")
		project.task("mlDeployPrivileges", type: DeployPrivilegesTask, group: securityGroup, description: "Deploy each privilege, updating it if it exists, in the configuration directory")
		project.task("mlDeployProtectedCollections", type: DeployProtectedCollectionsTask, group: securityGroup, description: "Deploy each protected collection, updating it if it exists, in the configuration directory")
		project.task("mlDeployRoles", type: DeployRolesTask, group: securityGroup, description: "Deploy each role, updating it if it exists, in the configuration directory")
		project.task("mlDeploySecurity", type: DeploySecurityTask, group: securityGroup, description: "Deploy each security resource, updating it if it exists, in the configuration directory")
		project.task("mlDeployUsers", type: DeployUsersTask, group: securityGroup, description: "Deploy each user, updating it if it exists, in the configuration directory")
		project.task("mlUndeployAmps", type: UndeployAmpsTask, group: securityGroup, description: "Undeploy (delete) each amp in the configuration directory")
		project.task("mlUndeployCertificateTemplates", type: UndeployCertificateTemplatesTask, group: securityGroup, description: "Undeploy (delete) each certificate template in the configuration directory")
		project.task("mlUndeployExternalSecurity", type: UndeployExternalSecurityTask, group: securityGroup, description: "Undeploy (delete) each external security configuration in the configuration directory")
		project.task("mlUndeployPrivileges", type: UndeployPrivilegesTask, group: securityGroup, description: "Undeploy (delete) each privilege in the configuration directory")
		project.task("mlUndeployProtectedCollections", type: UndeployProtectedCollectionsTask, group: securityGroup, description: "Undeploy (delete) each protected collection in the configuration directory")
		project.task("mlUndeployRoles", type: UndeployRolesTask, group: securityGroup, description: "Undeploy (delete) each role in the configuration directory")
		project.task("mlUndeployUsers", type: UndeployUsersTask, group: securityGroup, description: "Undeploy (delete) each user in the configuration directory")
		project.task("mlUndeploySecurity", type: UndeploySecurityTask, group: securityGroup, description: "Undeploy (delete) all security resources in the configuration directory")

		String sqlGroup = "ml-gradle SQL"
		project.task("mlDeployViewSchemas", type: DeployViewSchemasTask, group: sqlGroup, description: "Deploy each SQL view schema, updating it if it exists, in the configuration directory")

		String taskGroup = "ml-gradle Task"
		project.task("mlDeleteAllTasks", type: DeleteAllTasksTask, group: taskGroup, description: "Delete all scheduled tasks in the cluster")
		project.task("mlDeployTasks", type: DeployTasksTask, group: taskGroup, description: "Deploy each scheduled task, updating it if it exists, in the configuration directory")
		project.task("mlUndeployTasks", type: UndeployTasksTask, group: taskGroup, description: "Undeploy (delete) each scheduled task in the configuration directory")
		project.task("mlWaitForTaskServer", type: WaitForTaskServerTask, group: taskGroup, description: "Wait for the task server to not have any requests in progress")

		String temporalGroup = "ml-gradle Temporal"
		project.task("mlDeployTemporal", type: DeployTemporalTask, group: temporalGroup, description: "Deploy temporal configuration. Note that (currently) you can't update the temporal configuration (collection properties and LSQT properties excepted)")

		String triggerGroup = "ml-gradle Trigger"
		project.task("mlDeployTriggers", type: DeployTriggersTask, group: triggerGroup, description: "Deploy each trigger, updating it if it exists, in the configuration directory")

		String generalGroup = "ml-gradle General"
		project.task("mlPrintCommands", type: PrintCommandsTask, group: generalGroup, description: "Print information about each command used by mlDeploy and mlUndeploy")

		String shellGroup = "ml-gradle Shell"
		project.task("mlShell", type: ShellTask, group: shellGroup, description: "Run groovysh with MarkLogic-specific support built in")

		String roxyGroup = "ml-gradle Roxy";
		project.task("mlRoxyCopyProperties", type: RoxyCopyPropertiesTask, group: roxyGroup, description: "Copy Roxy properties to gradle.properties file")
		project.task("mlRoxyCopyFiles", type: RoxyCopyFilesTask, group: roxyGroup, description: "Copy roxy files")

		logger.info("Finished initializing ml-gradle\n")
	}

	void initializeAppDeployerObjects(Project project) {
		AdminConfig adminConfig = new DefaultAdminConfigFactory(new ProjectPropertySource(project)).newAdminConfig()
		project.extensions.add("mlAdminConfig", adminConfig)

		ProjectPropertySource propertySource = new ProjectPropertySource(project);
		AppConfig appConfig = new DefaultAppConfigFactory(propertySource).newAppConfig()
		if (appConfig.isReplaceTokensInModules()) {
			appConfig.getModuleTokensPropertiesSources().add(propertySource);
		}
		project.extensions.add("mlAppConfig", appConfig)

		ManageConfig manageConfig = new DefaultManageConfigFactory(new ProjectPropertySource(project)).newManageConfig()
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

	/**
	 * When the MarkLogic DatabaseClient class is used in Gradle, the Jersey ProviderServices class spits out
	 * a lot of not helpful logging at the INFO level. So we bump it down to WARNING to avoid that.
	 */
	void quietDownJerseyLogging() {
		try {
			Logger.getLogger(ProviderServices.class.getName()).setLevel(Level.WARNING)
		} catch (Exception e) {
			// Ignore, not important
		}
	}
}
