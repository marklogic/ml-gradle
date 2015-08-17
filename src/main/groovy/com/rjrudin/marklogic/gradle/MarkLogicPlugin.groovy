package com.rjrudin.marklogic.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory;

import com.rjrudin.marklogic.appdeployer.AppConfig
import com.rjrudin.marklogic.appdeployer.AppDeployer
import com.rjrudin.marklogic.appdeployer.ConfigDir
import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.appdeployer.command.CommandContext
import com.rjrudin.marklogic.appdeployer.command.appservers.DeployOtherServersCommand
import com.rjrudin.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployDomainsCommand
import com.rjrudin.marklogic.appdeployer.command.cpf.DeployPipelinesCommand
import com.rjrudin.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand
import com.rjrudin.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand
import com.rjrudin.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand
import com.rjrudin.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.rjrudin.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployAmpsCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployCertificateTemplatesCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployExternalSecurityCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployPrivilegesCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployRolesCommand
import com.rjrudin.marklogic.appdeployer.command.security.DeployUsersCommand
import com.rjrudin.marklogic.appdeployer.command.tasks.DeployScheduledTasksCommand
import com.rjrudin.marklogic.appdeployer.command.viewschemas.DeployViewSchemasCommand
import com.rjrudin.marklogic.appdeployer.impl.SimpleAppDeployer
import com.rjrudin.marklogic.gradle.task.DeleteModuleTimestampsFileTask
import com.rjrudin.marklogic.gradle.task.DeployAppTask
import com.rjrudin.marklogic.gradle.task.UndeployAppTask
import com.rjrudin.marklogic.gradle.task.admin.InitTask
import com.rjrudin.marklogic.gradle.task.admin.InstallAdminTask
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
import com.rjrudin.marklogic.gradle.task.databases.UpdateContentDatabasesTask
import com.rjrudin.marklogic.gradle.task.scaffold.GenerateScaffoldTask
import com.rjrudin.marklogic.gradle.task.security.DeploySecurityTask
import com.rjrudin.marklogic.gradle.task.servers.UpdateRestApiServersTask
import com.rjrudin.marklogic.gradle.task.tasks.DeployTasksTask
import com.rjrudin.marklogic.gradle.task.viewschemas.DeployViewSchemasTask
import com.rjrudin.marklogic.mgmt.ManageClient
import com.rjrudin.marklogic.mgmt.ManageConfig
import com.rjrudin.marklogic.mgmt.admin.AdminConfig
import com.rjrudin.marklogic.mgmt.admin.AdminManager

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
        project.task("mlDeploy", group: deployGroup, dependsOn: ["mlDeployApp", "mlPostDeploy"], description: "Deploys the application and allows for additional steps via mlPostDeploy.dependsOn")
        project.task("mlUndeploy", group: deployGroup, dependsOn: ["mlUndeployApp", "mlPostUndeploy"], description: "Undeploys the application and allows for additional steps via mlPostUndeploy.dependsOn")

        String adminGroup = "ml-gradle Admin"
        project.task("mlInit", type: InitTask, group: adminGroup, description: "Perform a one-time initialization of a MarkLogic server")
        project.task("mlInstallAdmin", type: InstallAdminTask, group: adminGroup, description: "Perform a one-time installation of an admin user")

        String cpfGroup = "ml-gradle CPF"
        project.task("mlDeployCpf", type: DeployCpfTask, group: cpfGroup, description: "Deploy only CPF pipelines, domains, and configurations").mustRunAfter("mlClearTriggersDatabase")
        project.task("mlRedeployCpf", group: cpfGroup, dependsOn: ["mlClearTriggersDatabase", "mlDeployCpf"], description: "Clears the triggers database and then calls mlDeployCpf; be sure to reload custom triggers after doing this, as they will be deleted as well")
        project.task("mlLoadDefaultPipelines", type: LoadDefaultPipelinesTask, group: cpfGroup, description: "Load default pipelines into a triggers database")

        String dbGroup = "ml-gradle Database"
        project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: dbGroup, description: "Deletes all documents in the content database; requires -PdeleteAll=true to be set so you don't accidentally do this")
        project.task("mlClearModulesDatabase", type: ClearModulesDatabaseTask, group: dbGroup, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")
        project.task("mlClearSchemasDatabase", type: ClearSchemasDatabaseTask, group: dbGroup, description: "Deletes all documents in the schemas database")
        project.task("mlClearTriggersDatabase", type: ClearTriggersDatabaseTask, group: dbGroup, description: "Deletes all documents in the triggers database")
        project.task("mlUpdateContentDatabase", type: UpdateContentDatabasesTask, group: dbGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the content databases based on the content database configuration file(s)")

        String devGroup = "ml-gradle Development"
        project.task("mlScaffold", type: GenerateScaffoldTask, group: devGroup, description: "Generate project scaffold for a new project")
        project.task("mlCreateResource", type: CreateResourceTask, group: devGroup, description: "Create a new resource extension in the modules services directory")
        project.task("mlCreateTransform", type: CreateTransformTask, group: devGroup, description: "Create a new transform in the modules transforms directory")
        project.task("mlPrepareRestApiDependencies", type: PrepareRestApiDependenciesTask, group: devGroup, dependsOn: project.configurations["mlRestApi"], description: "Downloads (if necessary) and unzips in the build directory all mlRestApi dependencies")

        String modulesGroup = "ml-gradle Modules"
        project.task("mlLoadModules", type: LoadModulesTask, group: modulesGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter(["mlClearModulesDatabase"])
        project.task("mlReloadModules", group: modulesGroup, dependsOn: ["mlClearModulesDatabase", "mlLoadModules"], description: "Reloads modules by first clearing the modules database and then loading modules")
        project.task("mlWatch", type: WatchTask, group: modulesGroup, description: "Run a loop that checks for new/modified modules every second and loads any that it finds")
        project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: modulesGroup, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded")

        String serverGroup = "ml-gradle Server"
        project.task("mlUpdateRestApiServers", type: UpdateRestApiServersTask, group: serverGroup, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the REST API servers")

        String securityGroup = "ml-gradle Security"
        project.task("mlDeploySecurity", type: DeploySecurityTask, group: securityGroup, description: "Deploy only security resources")

        String sqlGroup = "ml-gradle SQL"
        project.task("mlDeployViewSchemas", type: DeployViewSchemasTask, group: sqlGroup, description: "Deploy only SQL view schemas")

        String taskGroup = "ml-gradle Tasks"
        project.task("mlDeployTasks", type: DeployTasksTask, group: taskGroup, description: "Deploy only scheduled tasks")

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
        if (project.hasProperty("mlAppName")) {
            def name = project.property("mlAppName")
            logger.info("App name: " + name)
            appConfig.setName(name)
        }
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            logger.info("App host: " + host)
            appConfig.setHost(host)
        }

        if (project.hasProperty("mlRestPort")) {
            def port = project.property("mlRestPort")
            logger.info("App REST port: " + port)
            appConfig.setRestPort(Integer.parseInt(port))
        }
        if (project.hasProperty("mlTestRestPort")) {
            def port = project.property("mlTestRestPort")
            logger.info("App test REST port: " + port)
            appConfig.setTestRestPort(Integer.parseInt(port))
        }

        String restUsername = null
        if (project.hasProperty("mlRestAdminUsername")) {
            restUsername = project.property("mlRestAdminUsername")
        }
        else if (project.hasProperty("mlUsername")) {
            restUsername = project.property("mlUsername")
        }
        if (restUsername != null) {
            logger.info("REST Admin username: " + restUsername)
            appConfig.setRestAdminUsername(restUsername)
        }

        String restPassword = null
        if (project.hasProperty("mlRestAdminPassword")) {
            restPassword = project.property("mlRestAdminPassword")
        }
        else if (project.hasProperty("mlPassword")) {
            restPassword = project.property("mlPassword")
        }
        if (restPassword != null) {
            appConfig.setRestAdminPassword(restPassword)
        }

        project.extensions.add("mlAppConfig", appConfig)
    }

    void initializeManageConfig(Project project) {
        ManageConfig manageConfig = new ManageConfig()
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            logger.info("Manage host: " + host)
            manageConfig.setHost(host)
        }

        String username = null
        if (project.hasProperty("mlManageUsername")) {
            username = project.property("mlManageUsername")
        }
        else if (project.hasProperty("mlUsername")) {
            username = project.property("mlUsername")
        }
        if (username != null) {
            logger.info("Manage username: " + username)
            manageConfig.setUsername(username)
        }

        String password = null
        if (project.hasProperty("mlManagePassword")) {
            password = project.property("mlManagePassword")
        }
        else if (project.hasProperty("mlPassword")) {
            password = project.property("mlPassword")
        }
        if (password != null) {
            manageConfig.setPassword(password)
        }

        if (project.hasProperty("mlAdminUsername")) {
            manageConfig.setAdminUsername(project.property("mlAdminUsername"))
        }
        if (project.hasProperty("mlAdminPassword")) {
            manageConfig.setAdminPassword(project.property("mlAdminPassword"))
        }

        project.extensions.add("mlManageConfig", manageConfig)
    }

    void initializeAdminConfig(Project project) {
        AdminConfig adminConfig = new AdminConfig()
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            logger.info("Admin host: " + host)
            adminConfig.setHost(host)
        }

        String username = null
        if (project.hasProperty("mlAdminUsername")) {
            username = project.property("mlAdminUsername")
        }
        else if (project.hasProperty("mlUsername")) {
            username = project.property("mlUsername")
        }
        if (username != null) {
            logger.info("Admin username: " + username)
            adminConfig.setUsername(username)
        }

        String password = null
        if (project.hasProperty("mlAdminPassword")) {
            password = project.property("mlAdminPassword")
        }
        else if (project.hasProperty("mlPassword")) {
            password = project.property("mlPassword")
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

        project.extensions.add("mlAppDeployer", newAppDeployer(project, manageClient, adminManager))
    }

    /**
     * Creates an AppDeployer with a default set of commands. A developer can then modify this in an
     * ext block.
     */
    AppDeployer newAppDeployer(Project project, ManageClient manageClient, AdminManager adminManager) {
        List<Command> commands = new ArrayList<Command>()

        // Security
        List<Command> securityCommands = new ArrayList<Command>()
        securityCommands.add(new DeployRolesCommand())
        securityCommands.add(new DeployUsersCommand())
        securityCommands.add(new DeployAmpsCommand())
        securityCommands.add(new DeployCertificateTemplatesCommand())
        securityCommands.add(new DeployExternalSecurityCommand())
        securityCommands.add(new DeployPrivilegesCommand())
        securityCommands.add(new DeployProtectedCollectionsCommand())
        project.extensions.add("mlSecurityCommands", securityCommands)
        commands.addAll(securityCommands)

        // Databases and appservers
        commands.add(new DeployRestApiServersCommand())
        commands.add(new DeployTriggersDatabaseCommand())
        commands.add(new DeploySchemasDatabaseCommand())
        commands.add(new DeployOtherServersCommand())
        commands.add(new UpdateRestApiServersCommand())
        
        DeployContentDatabasesCommand dcdc = new DeployContentDatabasesCommand()
        if (project.hasProperty("mlContentForestsPerHost")) {
            dcdc.setForestsPerHost(Integer.parseInt(project.property("mlContentForestsPerHost")))
        }
        commands.add(dcdc)

        // Modules
        commands.add(new LoadModulesCommand())

        // CPF
        List<Command> cpfCommands = new ArrayList<Command>()
        cpfCommands.add(new DeployCpfConfigsCommand())
        cpfCommands.add(new DeployDomainsCommand())
        cpfCommands.add(new DeployPipelinesCommand())
        project.extensions.add("mlCpfCommands", cpfCommands)
        commands.addAll(cpfCommands)

        // Tasks
        List<Command> taskCommands = new ArrayList<Command>()
        taskCommands.add(new DeployScheduledTasksCommand())
        project.extensions.add("mlTaskCommands", taskCommands)
        commands.addAll(taskCommands)

        // SQL Views
        List<Command> viewCommands = new ArrayList<Command>()
        viewCommands.add(new DeployViewSchemasCommand())
        project.extensions.add("mlViewCommands", viewCommands)
        commands.addAll(viewCommands)

        SimpleAppDeployer deployer = new SimpleAppDeployer(manageClient, adminManager)
        deployer.setCommands(commands)
        return deployer
    }
}
