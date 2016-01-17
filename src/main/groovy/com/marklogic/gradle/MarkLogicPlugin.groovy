package com.marklogic.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.DefaultAppConfigFactory
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandContext
import com.marklogic.appdeployer.command.alert.DeployAlertActionsCommand
import com.marklogic.appdeployer.command.alert.DeployAlertConfigsCommand
import com.marklogic.appdeployer.command.alert.DeployAlertRulesCommand
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand
import com.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand
import com.marklogic.appdeployer.command.cpf.DeployDomainsCommand
import com.marklogic.appdeployer.command.cpf.DeployPipelinesCommand
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand
import com.marklogic.appdeployer.command.flexrep.DeployConfigsCommand
import com.marklogic.appdeployer.command.flexrep.DeployTargetsCommand
import com.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand
import com.marklogic.appdeployer.command.groups.DeployGroupsCommand
import com.marklogic.appdeployer.command.mimetypes.DeployMimetypesCommand
import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand
import com.marklogic.appdeployer.command.security.DeployAmpsCommand
import com.marklogic.appdeployer.command.security.DeployCertificateAuthoritiesCommand
import com.marklogic.appdeployer.command.security.DeployCertificateTemplatesCommand
import com.marklogic.appdeployer.command.security.DeployExternalSecurityCommand
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand
import com.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand
import com.marklogic.appdeployer.command.security.DeployRolesCommand
import com.marklogic.appdeployer.command.security.DeployUsersCommand
import com.marklogic.appdeployer.command.tasks.DeployScheduledTasksCommand
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand
import com.marklogic.appdeployer.command.viewschemas.DeployViewSchemasCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.DeleteModuleTimestampsFileTask
import com.marklogic.gradle.task.DeployAppTask
import com.marklogic.gradle.task.PrintCommandsTask
import com.marklogic.gradle.task.UndeployAppTask
import com.marklogic.gradle.task.admin.InitTask
import com.marklogic.gradle.task.admin.InstallAdminTask
import com.marklogic.gradle.task.alert.DeleteAllAlertConfigsTask
import com.marklogic.gradle.task.alert.DeployAlertingTask
import com.marklogic.gradle.task.client.CreateResourceTask
import com.marklogic.gradle.task.client.CreateTransformTask
import com.marklogic.gradle.task.client.LoadModulesTask
import com.marklogic.gradle.task.client.PrepareRestApiDependenciesTask
import com.marklogic.gradle.task.client.WatchTask
import com.marklogic.gradle.task.cluster.DisableSslFipsTask
import com.marklogic.gradle.task.cluster.EnableSslFipsTask
import com.marklogic.gradle.task.cluster.RestartClusterTask
import com.marklogic.gradle.task.cpf.DeployCpfTask
import com.marklogic.gradle.task.cpf.LoadDefaultPipelinesTask
import com.marklogic.gradle.task.databases.ClearContentDatabaseTask
import com.marklogic.gradle.task.databases.ClearModulesDatabaseTask
import com.marklogic.gradle.task.databases.ClearSchemasDatabaseTask
import com.marklogic.gradle.task.databases.ClearTriggersDatabaseTask
import com.marklogic.gradle.task.databases.DeployDatabasesTask
import com.marklogic.gradle.task.flexrep.DeleteAllFlexrepConfigsTask
import com.marklogic.gradle.task.flexrep.DeployFlexrepTask
import com.marklogic.gradle.task.flexrep.DisableAllFlexrepTargetsTask
import com.marklogic.gradle.task.flexrep.EnableAllFlexrepTargetsTask
import com.marklogic.gradle.task.forests.ConfigureForestReplicasTask
import com.marklogic.gradle.task.forests.DeleteForestReplicasTask
import com.marklogic.gradle.task.forests.DeployForestReplicasTask
import com.marklogic.gradle.task.groups.DeployGroupsTask
import com.marklogic.gradle.task.mimetypes.DeployMimetypesTask
import com.marklogic.gradle.task.scaffold.GenerateScaffoldTask
import com.marklogic.gradle.task.security.DeployAmpsTask
import com.marklogic.gradle.task.security.DeployCertificateAuthoritiesTask
import com.marklogic.gradle.task.security.DeployCertificateTemplatesTask
import com.marklogic.gradle.task.security.DeployExternalSecurityTask
import com.marklogic.gradle.task.security.DeployPrivilegesTask
import com.marklogic.gradle.task.security.DeployProtectedCollectionsTask
import com.marklogic.gradle.task.security.DeployRolesTask
import com.marklogic.gradle.task.security.DeploySecurityTask
import com.marklogic.gradle.task.security.DeployUsersTask
import com.marklogic.gradle.task.security.UndeployAmpsTask
import com.marklogic.gradle.task.security.UndeployCertificateTemplatesTask
import com.marklogic.gradle.task.security.UndeployExternalSecurityTask
import com.marklogic.gradle.task.security.UndeployPrivilegesTask
import com.marklogic.gradle.task.security.UndeployProtectedCollectionsTask
import com.marklogic.gradle.task.security.UndeployRolesTask
import com.marklogic.gradle.task.security.UndeploySecurityTask
import com.marklogic.gradle.task.security.UndeployUsersTask
import com.marklogic.gradle.task.servers.DeployServersTask
import com.marklogic.gradle.task.tasks.DeleteAllTasksTask
import com.marklogic.gradle.task.tasks.DeployTasksTask
import com.marklogic.gradle.task.tasks.UndeployTasksTask
import com.marklogic.gradle.task.trigger.DeployTriggersTask
import com.marklogic.gradle.task.viewschemas.DeployViewSchemasTask
import com.marklogic.mgmt.DefaultManageConfigFactory
import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.ManageConfig
import com.marklogic.mgmt.admin.AdminConfig
import com.marklogic.mgmt.admin.AdminManager
import com.marklogic.mgmt.admin.DefaultAdminConfigFactory

class MarkLogicPlugin implements Plugin<Project> {

    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass())

    void apply(Project project) {
        logger.info("\nInitializing ml-gradle")

        // Initialize groovysh support first so it doesn't pick up all the properties added when the AppDeployer is initialized
        initializeGroovyShellSupport(project)
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
        project.task("mlInit", type: InitTask, group: adminGroup, description: "Perform a one-time initialization of a MarkLogic server")
        project.task("mlInstallAdmin", type: InstallAdminTask, group: adminGroup, description: "Perform a one-time installation of an admin user")

        String alertGroup = "ml-gradle Alert"
        project.task("mlDeleteAllAlertConfigs", type: DeleteAllAlertConfigsTask, group: alertGroup, description: "Delete all alert configs, which also deletes all of the actions rules associated with them")
        project.task("mlDeployAlerting", type: DeployAlertingTask, group: alertGroup, description: "Deploy each alerting resource - configs, actions, and rules - in the configuration directory")

        String cpfGroup = "ml-gradle CPF"
        project.task("mlDeployCpf", type: DeployCpfTask, group: cpfGroup, description: "Deploy each CPF resource - domains, pipelines, and CPF configs - in the configuration directory").mustRunAfter("mlClearTriggersDatabase")
        project.task("mlRedeployCpf", group: cpfGroup, dependsOn: ["mlClearTriggersDatabase", "mlDeployCpf"], description: "Clears the triggers database and then calls mlDeployCpf; be sure to reload custom triggers after doing this, as they will be deleted as well")
        project.task("mlLoadDefaultPipelines", type: LoadDefaultPipelinesTask, group: cpfGroup, description: "Load default pipelines into a triggers database")

        String clusterGroup = "ml-gradle Cluster"
        project.task("mlDisableSslFips", type: DisableSslFipsTask, group: clusterGroup, description: "Disable SSL FIPS across the cluster")
        project.task("mlEnableSslFips", type: EnableSslFipsTask, group: clusterGroup, description: "Enable SSL FIPS across the cluster")
        project.task("mlRestartCluster", type: RestartClusterTask, group: clusterGroup, description: "Restart the cluster")

        String dbGroup = "ml-gradle Database"
        project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: dbGroup, description: "Deletes all documents in the content database; requires -PdeleteAll=true to be set so you don't accidentally do this")
        project.task("mlClearModulesDatabase", type: ClearModulesDatabaseTask, group: dbGroup, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")
        project.task("mlClearSchemasDatabase", type: ClearSchemasDatabaseTask, group: dbGroup, description: "Deletes all documents in the schemas database")
        project.task("mlClearTriggersDatabase", type: ClearTriggersDatabaseTask, group: dbGroup, description: "Deletes all documents in the triggers database")
        project.task("mlDeployDatabases", type: DeployDatabasesTask, group: dbGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Deploy each database, updating it if it exists, in the configuration directory")

        String devGroup = "ml-gradle Development"
        project.task("mlScaffold", type: GenerateScaffoldTask, group: devGroup, description: "Generate project scaffold for a new project")
        project.task("mlCreateResource", type: CreateResourceTask, group: devGroup, description: "Create a new resource extension in the modules services directory")
        project.task("mlCreateTransform", type: CreateTransformTask, group: devGroup, description: "Create a new transform in the modules transforms directory")
        project.task("mlPrepareRestApiDependencies", type: PrepareRestApiDependenciesTask, group: devGroup, dependsOn: project.configurations["mlRestApi"], description: "Downloads (if necessary) and unzips in the build directory all mlRestApi dependencies")

        String flexrepGroup = "ml-gradle Flexible Replication"
        project.task("mlDeleteAllFlexrepConfigs", type: DeleteAllFlexrepConfigsTask, group: flexrepGroup, description: "Delete all Flexrep configs and their associated targets")
        project.task("mlDeployFlexrep", type: DeployFlexrepTask, group: flexrepGroup, description: "Deploy Flexrep configs and targets in the configuration directory")
        project.task("mlDisableAllFlexrepTargets", type: DisableAllFlexrepTargetsTask, group: flexrepGroup, description: "Disable every target on every flexrep config")
        project.task("mlEnableAllFlexrepTargets", type: EnableAllFlexrepTargetsTask, group: flexrepGroup, description: "Enable every target on every flexrep config")

        String forestGroup = "ml-gradle Forest"
        project.task("mlConfigureForestReplicas", type: ConfigureForestReplicasTask, group: forestGroup, description: "Deprecated - configure forest replicas via the command.forestNamesAndReplicaCounts map")
        project.task("mlDeleteForestReplicas", type: DeleteForestReplicasTask, group: forestGroup, description: "Delete forest replicas via the command.forestNamesAndReplicaCounts map")
        project.task("mlDeployForestReplicas", type: DeployForestReplicasTask, group: forestGroup, description: "Prefer this over mlConfigureForestReplicas; it does the same thing, but uses the ConfigureForestReplicasCommand that is used by mlDeploy")

        String groupsGroup = "ml-gradle Group"
        project.task("mlDeployGroups", type: DeployGroupsTask, group: groupsGroup, description: "Deploy each group, updating it if it exists, in the configuration directory")
        
        String mimetypesGroup = "ml-gradle Mimetypes"
        project.task("mlDeployMimetypes", type: DeployMimetypesTask, group: mimetypesGroup, description: "Deploy each mimetype, updating it if it exists, in the configuration directory")
        
        String modulesGroup = "ml-gradle Modules"
        project.task("mlLoadModules", type: LoadModulesTask, group: modulesGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter(["mlClearModulesDatabase"])
        project.task("mlReloadModules", group: modulesGroup, dependsOn: ["mlClearModulesDatabase", "mlLoadModules"], description: "Reloads modules by first clearing the modules database and then loading modules")
        project.task("mlWatch", type: WatchTask, group: modulesGroup, description: "Run a loop that checks for new/modified modules every second and loads any that it finds")
        project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: modulesGroup, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded")

        String serverGroup = "ml-gradle Server"
        project.task("mlDeployServers", type: DeployServersTask, group: serverGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the REST API server (if it exists) and deploys each other server, updating it if it exists, in the configuration directory ")

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

        String triggerGroup = "ml-gradle Trigger"
        project.task("mlDeployTriggers", type: DeployTriggersTask, group: triggerGroup, description: "Deploy each trigger, updating it if it exists, in the configuration directory")

        String generalGroup = "ml-gradle General"
        project.task("mlPrintCommands", type: PrintCommandsTask, group: generalGroup, description: "Print information about each command used by mlDeploy and mlUndeploy")

        logger.info("Finished initializing ml-gradle\n")
    }

    void initializeAppDeployerObjects(Project project) {
        AdminConfig adminConfig = new DefaultAdminConfigFactory(new ProjectPropertySource(project)).newAdminConfig()
        project.extensions.add("mlAdminConfig", adminConfig)

        AppConfig appConfig = new DefaultAppConfigFactory(new ProjectPropertySource(project)).newAppConfig()
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

    void initializeGroovyShellSupport(Project project) {
        def mlShellJvmArgs = []
        for (String key : project.getProperties().keySet()) {
            if (key.startsWith("ml")) {
                mlShellJvmArgs.push("-D" + key + "=" + project.property(key))
            }
        }
        project.extensions.add("mlShellJvmArgs", mlShellJvmArgs)

        def script = "ml = com.marklogic.mgmt.api.APIUtil.newAPIFromSystemProps()"
        if (project.hasProperty("mlShellWatchModules") && project.property("mlShellWatchModules").equals("true")) {
            script += "\ncom.marklogic.appdeployer.util.ModulesWatcher.startFromSystemProps()"
        }

        // Allow project to add to the shell initialization script; mlShellScript will need to be in gradle.properties
        if (project.hasProperty("mlShellScript")) {
            script += "\n" + project.property("mlShellScript")
        }

        def mlShellArgs = ["-e", script]
        project.extensions.add("mlShellArgs", mlShellArgs)

        /**
         * If the groovysh plugin has already been applied, then we can jvmArgs and args on the shell task automatically.
         * Otherwise, the shell task needs to be configured in the Gradle file. 
         */
        if (project.getExtensions().findByName("groovysh")) {
            project.afterEvaluate {
                def task = project.tasks.shell
                task.jvmArgs = mlShellJvmArgs
                task.args = mlShellArgs
            }
        }
    }

    /**
     * Creates an AppDeployer with a default set of commands. A developer can then modify this in an
     * ext block.
     */
    AppDeployer newAppDeployer(Project project, CommandContext context) {
        List<Command> commands = new ArrayList<Command>()

        // Security
        List<Command> securityCommands = new ArrayList<Command>()
        securityCommands.add(new DeployRolesCommand())
        securityCommands.add(new DeployUsersCommand())
        securityCommands.add(new DeployAmpsCommand())
        securityCommands.add(new DeployCertificateTemplatesCommand())
        securityCommands.add(new DeployCertificateAuthoritiesCommand())
        securityCommands.add(new DeployExternalSecurityCommand())
        securityCommands.add(new DeployPrivilegesCommand())
        securityCommands.add(new DeployProtectedCollectionsCommand())
        project.extensions.add("mlSecurityCommands", securityCommands)
        commands.addAll(securityCommands)

        // Databases
        List<Command> dbCommands = new ArrayList<Command>()
        dbCommands.add(new DeployContentDatabasesCommand())
        dbCommands.add(new DeployTriggersDatabaseCommand())
        dbCommands.add(new DeploySchemasDatabaseCommand())
        project.extensions.add("mlDatabaseCommands", dbCommands)
        commands.addAll(dbCommands)

        // REST API instance creation
        commands.add(new DeployRestApiServersCommand())

        // App servers
        List<Command> serverCommands = new ArrayList<Command>()
        serverCommands.add(new DeployOtherServersCommand())
        serverCommands.add(new UpdateRestApiServersCommand())
        project.extensions.add("mlServerCommands", serverCommands)
        commands.addAll(serverCommands)

        // Modules
        LoadModulesCommand lmc = new LoadModulesCommand()
        lmc.initializeDefaultModulesLoader(context)
        project.extensions.add("mlLoadModulesCommand", lmc)
        commands.add(lmc)

        // Alerting
        List<Command> alertCommands = new ArrayList<Command>()
        alertCommands.add(new DeployAlertConfigsCommand())
        alertCommands.add(new DeployAlertActionsCommand())
        alertCommands.add(new DeployAlertRulesCommand())
        project.extensions.add("mlAlertCommands", alertCommands)
        commands.addAll(alertCommands)

        // CPF
        List<Command> cpfCommands = new ArrayList<Command>()
        cpfCommands.add(new DeployCpfConfigsCommand())
        cpfCommands.add(new DeployDomainsCommand())
        cpfCommands.add(new DeployPipelinesCommand())
        project.extensions.add("mlCpfCommands", cpfCommands)
        commands.addAll(cpfCommands)

        // Flexrep
        List<Command> flexrepCommands = new ArrayList<Command>()
        flexrepCommands.add(new DeployConfigsCommand())
        flexrepCommands.add(new DeployTargetsCommand())
        project.extensions.add("mlFlexrepCommands", flexrepCommands)
        commands.addAll(flexrepCommands)

        // Groups
        List<Command> groupCommands = new ArrayList<Command>()
        groupCommands.add(new DeployGroupsCommand())
        project.extensions.add("mlGroupCommands", groupCommands)
        commands.addAll(groupCommands)

        List<Command> mimetypeCommands = new ArrayList<Command>()
        mimetypeCommands.add(new DeployMimetypesCommand())
        project.extensions.add("mlMimetypeCommands", mimetypeCommands)
        commands.addAll(mimetypeCommands)
        
        // Forest replicas
        List<Command> replicaCommands = new ArrayList<Command>()
        replicaCommands.add(new ConfigureForestReplicasCommand())
        project.extensions.add("mlForestReplicaCommands", replicaCommands)
        commands.addAll(replicaCommands)

        // Tasks
        List<Command> taskCommands = new ArrayList<Command>()
        taskCommands.add(new DeployScheduledTasksCommand())
        project.extensions.add("mlTaskCommands", taskCommands)
        commands.addAll(taskCommands)

        // Triggers
        List<Command> triggerCommands = new ArrayList<Command>()
        triggerCommands.add(new DeployTriggersCommand())
        project.extensions.add("mlTriggerCommands", triggerCommands)
        commands.addAll(triggerCommands)

        // SQL Views
        List<Command> viewCommands = new ArrayList<Command>()
        viewCommands.add(new DeployViewSchemasCommand())
        project.extensions.add("mlViewCommands", viewCommands)
        commands.addAll(viewCommands)

        SimpleAppDeployer deployer = new SimpleAppDeployer(context.getManageClient(), context.getAdminManager())
        deployer.setCommands(commands)
        return deployer
    }
}
