package com.rjrudin.marklogic.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

import com.marklogic.client.DatabaseClientFactory
import com.rjrudin.marklogic.appdeployer.AppConfig
import com.rjrudin.marklogic.appdeployer.AppDeployer
import com.rjrudin.marklogic.appdeployer.ConfigDir
import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.appdeployer.command.CommandContext
import com.rjrudin.marklogic.appdeployer.command.alert.DeployAlertActionsCommand
import com.rjrudin.marklogic.appdeployer.command.alert.DeployAlertConfigsCommand
import com.rjrudin.marklogic.appdeployer.command.alert.DeployAlertRulesCommand
import com.rjrudin.marklogic.appdeployer.command.appservers.DeployOtherServersCommand
import com.rjrudin.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployDomainsCommand
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployPipelinesCommand
import com.rjrudin.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand
import com.rjrudin.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand
import com.rjrudin.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand
import com.rjrudin.marklogic.appdeployer.command.flexrep.DeployConfigsCommand
import com.rjrudin.marklogic.appdeployer.command.flexrep.DeployTargetsCommand
import com.rjrudin.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand
import com.rjrudin.marklogic.appdeployer.command.groups.DeployGroupsCommand
import com.rjrudin.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.rjrudin.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployAmpsCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployCertificateAuthoritiesCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployCertificateTemplatesCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployExternalSecurityCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployPrivilegesCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployRolesCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployUsersCommand
import com.rjrudin.marklogic.appdeployer.command.tasks.DeployScheduledTasksCommand
import com.rjrudin.marklogic.appdeployer.command.triggers.DeployTriggersCommand
import com.rjrudin.marklogic.appdeployer.command.viewschemas.DeployViewSchemasCommand
import com.rjrudin.marklogic.appdeployer.impl.SimpleAppDeployer
import com.rjrudin.marklogic.gradle.task.DeleteModuleTimestampsFileTask
import com.rjrudin.marklogic.gradle.task.DeployAppTask
import com.rjrudin.marklogic.gradle.task.PrintCommandsTask
import com.rjrudin.marklogic.gradle.task.UndeployAppTask
import com.rjrudin.marklogic.gradle.task.admin.InitTask
import com.rjrudin.marklogic.gradle.task.admin.InstallAdminTask
import com.rjrudin.marklogic.gradle.task.alert.DeployAlertingTask
import com.rjrudin.marklogic.gradle.task.client.CreateResourceTask
import com.rjrudin.marklogic.gradle.task.client.CreateTransformTask
import com.rjrudin.marklogic.gradle.task.client.LoadModulesTask
import com.rjrudin.marklogic.gradle.task.client.PrepareRestApiDependenciesTask
import com.rjrudin.marklogic.gradle.task.client.WatchTask
import com.rjrudin.marklogic.gradle.task.cpf.DeployCpfTask
import com.rjrudin.marklogic.gradle.task.cpf.LoadDefaultPipelinesTask
import com.rjrudin.marklogic.gradle.task.databases.ClearContentDatabaseTask
import com.rjrudin.marklogic.gradle.task.databases.ClearModulesDatabaseTask
import com.rjrudin.marklogic.gradle.task.databases.ClearSchemasDatabaseTask
import com.rjrudin.marklogic.gradle.task.databases.ClearTriggersDatabaseTask
import com.rjrudin.marklogic.gradle.task.databases.DeployDatabasesTask
import com.rjrudin.marklogic.gradle.task.flexrep.DeployFlexrepTask
import com.rjrudin.marklogic.gradle.task.forests.ConfigureForestReplicasTask
import com.rjrudin.marklogic.gradle.task.forests.DeleteForestReplicasTask
import com.rjrudin.marklogic.gradle.task.forests.DeployForestReplicasTask
import com.rjrudin.marklogic.gradle.task.groups.DeployGroupsTask
import com.rjrudin.marklogic.gradle.task.scaffold.GenerateScaffoldTask
import com.rjrudin.marklogic.gradle.task.security.DeployAmpsTask
import com.rjrudin.marklogic.gradle.task.security.DeployCertificateAuthoritiesTask
import com.rjrudin.marklogic.gradle.task.security.DeployCertificateTemplatesTask
import com.rjrudin.marklogic.gradle.task.security.DeployExternalSecurityTask
import com.rjrudin.marklogic.gradle.task.security.DeployPrivilegesTask
import com.rjrudin.marklogic.gradle.task.security.DeployProtectedCollectionsTask
import com.rjrudin.marklogic.gradle.task.security.DeployRolesTask
import com.rjrudin.marklogic.gradle.task.security.DeploySecurityTask
import com.rjrudin.marklogic.gradle.task.security.DeployUsersTask
import com.rjrudin.marklogic.gradle.task.security.UndeployAmpsTask
import com.rjrudin.marklogic.gradle.task.security.UndeployCertificateTemplatesTask
import com.rjrudin.marklogic.gradle.task.security.UndeployExternalSecurityTask
import com.rjrudin.marklogic.gradle.task.security.UndeployPrivilegesTask
import com.rjrudin.marklogic.gradle.task.security.UndeployProtectedCollectionsTask
import com.rjrudin.marklogic.gradle.task.security.UndeployRolesTask
import com.rjrudin.marklogic.gradle.task.security.UndeploySecurityTask
import com.rjrudin.marklogic.gradle.task.security.UndeployUsersTask
import com.rjrudin.marklogic.gradle.task.servers.DeployServersTask
import com.rjrudin.marklogic.gradle.task.tasks.DeployTasksTask
import com.rjrudin.marklogic.gradle.task.tasks.UndeployTasksTask
import com.rjrudin.marklogic.gradle.task.trigger.DeployTriggersTask;
import com.rjrudin.marklogic.gradle.task.viewschemas.DeployViewSchemasTask
import com.rjrudin.marklogic.mgmt.ManageClient
import com.rjrudin.marklogic.mgmt.ManageConfig
import com.rjrudin.marklogic.mgmt.admin.AdminConfig
import com.rjrudin.marklogic.mgmt.admin.AdminManager
import com.rjrudin.marklogic.modulesloader.ssl.SimpleX509TrustManager

class MarkLogicPlugin implements Plugin<Project> {

    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass())

    void apply(Project project) {
        logger.info("\nInitializing ml-gradle")

        initializeAppConfig(project)
        initializeManageConfig(project)
        initializeAdminConfig(project)
        initializeAppDeployerObjects(project)

        project.getConfigurations().create("mlRestApi")

        // No group or description on these so they don't show up in "gradle tasks"
        project.task("mlDeployApp", type: DeployAppTask, dependsOn: ["mlDeleteModuleTimestampsFile", "mlPrepareRestApiDependencies"])
        project.task("mlUndeployApp", type: UndeployAppTask)

        String deployGroup = "ml-gradle Deploy"
        project.task("mlPostDeploy", group: deployGroup, description: "Add dependsOn to this task to add tasks at the end of mlDeploy").mustRunAfter(["mlDeployApp"])
        project.task("mlPostUndeploy", group: deployGroup, description: "Add dependsOn to this task to add tasks at the end of mlUndeploy").mustRunAfter(["mlUndeployApp"])
        project.task("mlDeploy", group: deployGroup, dependsOn: ["mlDeployApp", "mlPostDeploy"], description: "Deploys the application and allows for additional steps via mlPostDeploy.dependsOn").mustRunAfter("mlClearModulesDatabase")
        project.task("mlUndeploy", group: deployGroup, dependsOn: ["mlUndeployApp", "mlPostUndeploy"], description: "Undeploys the application and allows for additional steps via mlPostUndeploy.dependsOn")
        project.task("mlRedeploy", group: deployGroup, dependsOn: ["mlClearModulesDatabase", "mlDeploy"], description: "Clears the modules database and then deploys the application")

        String adminGroup = "ml-gradle Admin"
        project.task("mlInit", type: InitTask, group: adminGroup, description: "Perform a one-time initialization of a MarkLogic server")
        project.task("mlInstallAdmin", type: InstallAdminTask, group: adminGroup, description: "Perform a one-time installation of an admin user")

        String alertGroup = "ml-gradle Alert"
        project.task("mlDeployAlerting", type: DeployAlertingTask, group: alertGroup, description: "Deploy each alerting resource - configs, actions, and rules")
        
        String cpfGroup = "ml-gradle CPF"
        project.task("mlDeployCpf", type: DeployCpfTask, group: cpfGroup, description: "Deploy each CPF resource - domains, pipelines, and CPF configs").mustRunAfter("mlClearTriggersDatabase")
        project.task("mlRedeployCpf", group: cpfGroup, dependsOn: ["mlClearTriggersDatabase", "mlDeployCpf"], description: "Clears the triggers database and then calls mlDeployCpf; be sure to reload custom triggers after doing this, as they will be deleted as well")
        project.task("mlLoadDefaultPipelines", type: LoadDefaultPipelinesTask, group: cpfGroup, description: "Load default pipelines into a triggers database")

        String dbGroup = "ml-gradle Database"
        project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: dbGroup, description: "Deletes all documents in the content database; requires -PdeleteAll=true to be set so you don't accidentally do this")
        project.task("mlClearModulesDatabase", type: ClearModulesDatabaseTask, group: dbGroup, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")
        project.task("mlClearSchemasDatabase", type: ClearSchemasDatabaseTask, group: dbGroup, description: "Deletes all documents in the schemas database")
        project.task("mlClearTriggersDatabase", type: ClearTriggersDatabaseTask, group: dbGroup, description: "Deletes all documents in the triggers database")
        project.task("mlDeployDatabases", type: DeployDatabasesTask, group: dbGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Deploy each database, updating it if it exists")

        String devGroup = "ml-gradle Development"
        project.task("mlScaffold", type: GenerateScaffoldTask, group: devGroup, description: "Generate project scaffold for a new project")
        project.task("mlCreateResource", type: CreateResourceTask, group: devGroup, description: "Create a new resource extension in the modules services directory")
        project.task("mlCreateTransform", type: CreateTransformTask, group: devGroup, description: "Create a new transform in the modules transforms directory")
        project.task("mlPrepareRestApiDependencies", type: PrepareRestApiDependenciesTask, group: devGroup, dependsOn: project.configurations["mlRestApi"], description: "Downloads (if necessary) and unzips in the build directory all mlRestApi dependencies")

        String flexrepGroup = "ml-gradle Flexible Replication"
        project.task("mlDeployFlexrep", type: DeployFlexrepTask, group: flexrepGroup, description: "Deploy Flexrep configs and targets")

        String forestGroup = "ml-gradle Forest"
        project.task("mlConfigureForestReplicas", type: ConfigureForestReplicasTask, group: forestGroup, description: "Deprecated - configure forest replicas via the command.forestNamesAndReplicaCounts map")
        project.task("mlDeleteForestReplicas", type: DeleteForestReplicasTask, group: forestGroup, description: "Delete forest replicas via the command.forestNamesAndReplicaCounts map")
        project.task("mlDeployForestReplicas", type: DeployForestReplicasTask, group: forestGroup, description: "Prefer this over mlConfigureForestReplicas; it does the same thing, but uses the ConfigureForestReplicasCommand that is used by mlDeploy")
        
        String groupsGroup = "ml-gradle Group"
        project.task("mlDeployGroups", type: DeployGroupsTask, group: groupsGroup, description: "Deploy each group, updating it if it exists")

        String modulesGroup = "ml-gradle Modules"
        project.task("mlLoadModules", type: LoadModulesTask, group: modulesGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter(["mlClearModulesDatabase"])
        project.task("mlReloadModules", group: modulesGroup, dependsOn: ["mlClearModulesDatabase", "mlLoadModules"], description: "Reloads modules by first clearing the modules database and then loading modules")
        project.task("mlWatch", type: WatchTask, group: modulesGroup, description: "Run a loop that checks for new/modified modules every second and loads any that it finds")
        project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: modulesGroup, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded")

        String serverGroup = "ml-gradle Server"
        project.task("mlDeployServers", type: DeployServersTask, group: serverGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the REST API server (if it exists) and deploys each other server, updating it if it exists")

        String securityGroup = "ml-gradle Security"
        project.task("mlDeployAmps", type: DeployAmpsTask, group: securityGroup, description: "Deploy each amp, updating it if it exists")
        project.task("mlDeployCertificateAuthorities", type: DeployCertificateAuthoritiesTask, group: securityGroup, description: "Deploy each certificate authority, updating it if it exists")
        project.task("mlDeployCertificateTemplates", type: DeployCertificateTemplatesTask, group: securityGroup, description: "Deploy each certificate template, updating it if it exists")
        project.task("mlDeployExternalSecurity", type: DeployExternalSecurityTask, group: securityGroup, description: "Deploy external security configurations, updating each if it exists")
        project.task("mlDeployPrivileges", type: DeployPrivilegesTask, group: securityGroup, description: "Deploy each privilege, updating it if it exists")
        project.task("mlDeployProtectedCollections", type: DeployProtectedCollectionsTask, group: securityGroup, description: "Deploy each protected collection, updating it if it exists")
        project.task("mlDeployRoles", type: DeployRolesTask, group: securityGroup, description: "Deploy each role, updating it if it exists")
        project.task("mlDeploySecurity", type: DeploySecurityTask, group: securityGroup, description: "Deploy each security resource, updating it if it exists")
        project.task("mlDeployUsers", type: DeployUsersTask, group: securityGroup, description: "Deploy each user, updating it if it exists")
        project.task("mlUndeployAmps", type: UndeployAmpsTask, group: securityGroup, description: "Undeploy (delete) each amp")
        project.task("mlUndeployCertificateTemplates", type: UndeployCertificateTemplatesTask, group: securityGroup, description: "Undeploy (delete) each certificate template")
        project.task("mlUndeployExternalSecurity", type: UndeployExternalSecurityTask, group: securityGroup, description: "Undeploy (delete) each external security configuration")
        project.task("mlUndeployPrivileges", type: UndeployPrivilegesTask, group: securityGroup, description: "Undeploy (delete) each privilege")
        project.task("mlUndeployProtectedCollections", type: UndeployProtectedCollectionsTask, group: securityGroup, description: "Undeploy (delete) each protected collection")
        project.task("mlUndeployRoles", type: UndeployRolesTask, group: securityGroup, description: "Undeploy (delete) each role")
        project.task("mlUndeployUsers", type: UndeployUsersTask, group: securityGroup, description: "Undeploy (delete) each user")
        project.task("mlUndeploySecurity", type: UndeploySecurityTask, group: securityGroup, description: "Undeploy (delete) all security resources")
        
        String sqlGroup = "ml-gradle SQL"
        project.task("mlDeployViewSchemas", type: DeployViewSchemasTask, group: sqlGroup, description: "Deploy each SQL view schema, updating it if it exists")

        String taskGroup = "ml-gradle Task"
        project.task("mlDeployTasks", type: DeployTasksTask, group: taskGroup, description: "Deploy each scheduled task, updating it if it exists")
        project.task("mlUndeployTasks", type: UndeployTasksTask, group: taskGroup, description: "Undeploy (delete) each scheduled task")
        
        String triggerGroup = "ml-gradle Trigger"
        project.task("mlDeployTriggers", type: DeployTriggersTask, group: triggerGroup, description: "Deploy each trigger, updating it if it exists")
        
        String generalGroup = "ml-gradle General"
        project.task("mlPrintCommands", type: PrintCommandsTask, group: generalGroup, description: "Print information about each command used by mlDeploy and mlUndeploy")

        logger.info("Finished initializing ml-gradle\n")
    }

    /**
     * Read in certain project properties and use them to initialize an instance of AppConfig. The properties are typically
     * defined in gradle.properties.
     * 
     * @param project
     */
    void initializeAppConfig(Project project) {
        AppConfig appConfig = new AppConfig()
        if (project.hasProperty("mlConfigDir")) {
            def prop = project.property("mlConfigDir")
            logger.info("Setting config dir to: " + prop)
            appConfig.setConfigDir(new ConfigDir(new File(prop)))
        }
        
        def name = project.hasProperty("mlAppName") ? project.property("mlAppName") : project.property("marklogic.application.name")
        logger.info("App name: " + name)
        appConfig.setName(name)
      
        logger.info("App host: " + getHost(project))
        appConfig.setHost(getHost(project))
        
        def port = project.hasProperty("mlRestPort") ? project.property("mlRestPort") : project.property("marklogic.rest.port") 
        logger.info("App REST port: " + port)
        appConfig.setRestPort(Integer.parseInt(port))
        
        def testPort = project.hasProperty("mlTestRestPort") ? project.property("mlTestRestPort") : project.property("marklogic.rest.test.port")
        logger.info("App test REST port: " + testPort)
        appConfig.setTestRestPort(Integer.parseInt(testPort))

        String restUsername = project.hasProperty("mlRestAdminUsername") ? project.property("mlRestAdminUsername") : project.property("marklogic.admin.rest.username")
        if (restUsername) {
          restUsername = getUsername(project)
        }
        if (restUsername) {
            logger.info("REST Admin username: " + restUsername)
            appConfig.setRestAdminUsername(restUsername)
        }

        String restPassword = project.hasProperty("mlRestAdminPassword") ? project.property("mlRestAdminPassword") : project.property("marklogic.admin.rest.password")
        if (restPassword) {
            restPassword = getPassword(project)
        }
        if (restPassword) {
            appConfig.setRestAdminPassword(restPassword)
        }

        if (project.hasProperty("mlSimpleSsl") || project.hasProperty("marklogic.simple.ssl")) {
            logger.info("Using simple SSL context and 'ANY' hostname verifier for authenticating against client REST API server")
            appConfig.setRestSslContext(SimpleX509TrustManager.newSSLContext())
            appConfig.setRestSslHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
        }

        project.extensions.add("mlAppConfig", appConfig)
    }

    void initializeManageConfig(Project project) {
        ManageConfig manageConfig = new ManageConfig()
        
        logger.info("Manage host: " + getHost(project))
        manageConfig.setHost(getHost(project))
        

        String username = project.hasProperty("mlManageUsername") ? project.property("mlManageUsername") : project.property("marklogic.manage.username") 
        if (username) {
            username = getUsername(project)
        }        
        if (username) {
            logger.info("Manage username: " + username)
            manageConfig.setUsername(username)
        }
        
        String password = null
        if (project.hasProperty("mlManagePassword")) {
            password = project.property("mlManagePassword")
        }
        if (password) {
            password = getPassword(project)
        }
        if (password) {
            manageConfig.setPassword(password)
        }

        String adminUsername = project.hasProperty("mlAdminUsername") ? project.property("mlAdminUsername") : project.property("marklogic.admin.username")
        manageConfig.setAdminUsername(adminUsername)
        
        String adminPassword = project.hasProperty("mlAdminPassword") ? project.property("mlAdminPassword") : project.property("marklogic.admin.password")
        manageConfig.setAdminPassword(adminPassword)
        
        project.extensions.add("mlManageConfig", manageConfig)
    }

    void initializeAdminConfig(Project project) {
        AdminConfig adminConfig = new AdminConfig()

        logger.info("Admin host: " + getHost(project))
        adminConfig.setHost(getHost(project))

        String username = project.hasProperty("mlAdminUsername") ? project.property("mlAdminUsername") : project.property("marklogic.admin.username")
        if (username) {
            username = getUsername(project)
        }
        if (username != null) {
            logger.info("Admin username: " + username)
            adminConfig.setUsername(username)
        }

        String password = project.hasProperty("mlAdminPassword") ? project.property("mlAdminPassword") : project.property("marklogic.admin.password")
        if (password) {
            password = getPassword(project)
        }
        if (password != null) {
            adminConfig.setPassword(password)
        }

        project.extensions.add("mlAdminConfig", adminConfig)
    }

    void initializeAppDeployerObjects(Project project) {
        ManageConfig manageConfig = project.extensions.getByName("mlManageConfig")

        ManageClient manageClient = new ManageClient(manageConfig)
        project.extensions.add("mlManageClient", manageClient)

        AdminManager adminManager = new AdminManager(project.extensions.getByName("mlAdminConfig"))
        project.extensions.add("mlAdminManager", adminManager)

        CommandContext context = new CommandContext(project.extensions.getByName("mlAppConfig"), manageClient, adminManager)
        project.extensions.add("mlCommandContext", context)

        project.extensions.add("mlAppDeployer", newAppDeployer(project, context))
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
        DeployContentDatabasesCommand dcdc = new DeployContentDatabasesCommand()
        if (project.hasProperty("mlContentForestsPerHost")) {
            int num = Integer.parseInt(project.property("mlContentForestsPerHost"))
            logger.info("Setting content forests per host to: " + num)
            dcdc.setForestsPerHost(num)
        }
        dbCommands.add(dcdc)
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
        if (project.hasProperty("mlModulePermissions")) {
            String perms = project.property("mlModulePermissions")
            logger.info("Setting module permissions to: " + perms)
            lmc.setDefaultAssetRolesAndCapabilities(perms)
        }
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

        // Forest replicas
        List<Command> replicaCommands = new ArrayList<Command>()
        ConfigureForestReplicasCommand cfrc = new ConfigureForestReplicasCommand()
        if (project.hasProperty("mlForestReplicas")) {
            cfrc.setDatabaseNamesAndReplicaCountsAsString(project.property("mlForestReplicas"))
        }
        replicaCommands.add(cfrc)
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
    
    def getHost(project) { 
      project.hasProperty("mlHost") ? project.property("mlHost") : project.property("marklogic.host")
    }    
    
    def getUsername(project) {
      project.hasProperty("mlUsername") ? project.property("mlUsername") : project.property("marklogic.username")
    }
    
    def getPassword(project) {
      project.hasProperty("mlPassword") ? project.property("mlPassword") : project.property("marklogic.password")
    }
    
}
