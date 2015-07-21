package com.marklogic.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.ConfigDir
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandContext
import com.marklogic.appdeployer.command.appservers.CreateOtherServersCommand
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand
import com.marklogic.appdeployer.command.cpf.CreateCpfConfigsCommand
import com.marklogic.appdeployer.command.cpf.CreateDomainsCommand
import com.marklogic.appdeployer.command.cpf.CreatePipelinesCommand
import com.marklogic.appdeployer.command.databases.CreateSchemasDatabaseCommand
import com.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand
import com.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand
import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand
import com.marklogic.appdeployer.command.security.CreateAmpsCommand
import com.marklogic.appdeployer.command.security.CreateCertificateTemplatesCommand
import com.marklogic.appdeployer.command.security.CreateExternalSecurityCommand
import com.marklogic.appdeployer.command.security.CreatePrivilegesCommand
import com.marklogic.appdeployer.command.security.CreateProtectedCollectionsCommand
import com.marklogic.appdeployer.command.security.CreateRolesCommand
import com.marklogic.appdeployer.command.security.CreateUsersCommand
import com.marklogic.appdeployer.command.tasks.CreateScheduledTasksCommand
import com.marklogic.appdeployer.command.viewschemas.CreateViewSchemasCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.DeleteModuleTimestampsFileTask
import com.marklogic.gradle.task.DeployAppTask
import com.marklogic.gradle.task.UndeployAppTask
import com.marklogic.gradle.task.admin.InitTask
import com.marklogic.gradle.task.admin.InstallAdminTask
import com.marklogic.gradle.task.client.CreateResourceTask
import com.marklogic.gradle.task.client.CreateTransformTask
import com.marklogic.gradle.task.client.LoadModulesTask
import com.marklogic.gradle.task.client.PrepareRestApiDependenciesTask
import com.marklogic.gradle.task.client.WatchTask
import com.marklogic.gradle.task.cpf.DeployCpfTask
import com.marklogic.gradle.task.cpf.LoadDefaultPipelinesTask
import com.marklogic.gradle.task.databases.ClearContentDatabaseTask
import com.marklogic.gradle.task.databases.ClearModulesDatabaseTask
import com.marklogic.gradle.task.databases.ClearSchemasDatabaseTask
import com.marklogic.gradle.task.databases.ClearTriggersDatabaseTask
import com.marklogic.gradle.task.databases.UpdateContentDatabasesTask
import com.marklogic.gradle.task.scaffold.GenerateScaffoldTask
import com.marklogic.gradle.task.servers.UpdateRestApiServersTask
import com.marklogic.gradle.task.viewschemas.CreateViewSchemasTask
import com.marklogic.rest.mgmt.ManageClient
import com.marklogic.rest.mgmt.ManageConfig
import com.marklogic.rest.mgmt.admin.AdminConfig
import com.marklogic.rest.mgmt.admin.AdminManager

class MarkLogicPlugin implements Plugin<Project> {

    void apply(Project project) {
        println "\nInitializing ml-gradle"

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
        
        String sqlGroup = "ml-gradle SQL"
        project.task("mlCreateViewSchemas", type: CreateViewSchemasTask, group: sqlGroup, description: "Create or update SQL view schemas")
        
        println "Finished initializing ml-gradle\n"
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
            println "Setting config dir to: " + prop
            appConfig.setConfigDir(new ConfigDir(new File(prop)))
        }
        if (project.hasProperty("mlAppName")) {
            def name = project.property("mlAppName")
            println "App name: " + name
            appConfig.setName(name)
        }
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            println "App host: " + host
            appConfig.setHost(host)
        }
        if (project.hasProperty("mlUsername")) {
            def username = project.property("mlUsername")
            println "App username: " + username
            appConfig.setRestAdminUsername(username)
        }
        if (project.hasProperty("mlPassword")) {
            appConfig.setRestAdminPassword(project.property("mlPassword"))
        }

        if (project.hasProperty("mlRestPort")) {
            def port = project.property("mlRestPort")
            println "App REST port: " + port
            appConfig.setRestPort(Integer.parseInt(port))
        }
        if (project.hasProperty("mlTestRestPort")) {
            def port = project.property("mlTestRestPort")
            println "App test REST port: " + port
            appConfig.setTestRestPort(Integer.parseInt(port))
        }

        String xdbcUsername = null
        if (project.hasProperty("mlXdbcUsername")) {
            xdbcUsername = project.property("mlXdbcUsername")
        }
        else if (project.hasProperty("mlUsername")) {
            xdbcUsername = project.property("mlUsername")
        }
        if (xdbcUsername != null) {
            println "App XDBC username: " + xdbcUsername
            appConfig.setXdbcUsername(xdbcUsername)
        }
        
        String xdbcPassword = null
        if (project.hasProperty("mlXdbcPassword")) {
            xdbcPassword = project.property("mlXdbcPassword")
        }
        else if (project.hasProperty("mlPassword")) {
            xdbcPassword = project.property("mlPassword")
        }
        if (xdbcPassword != null) {
            appConfig.setXdbcPassword(xdbcPassword)
        }
        
        project.extensions.add("mlAppConfig", appConfig)
    }

    void initializeManageConfig(Project project) {
        ManageConfig manageConfig = new ManageConfig()
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            println "Manage host: " + host
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
            println "Manage username: " + username
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
            println "Admin host: " + host
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
            println "Admin username: " + username
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

        project.extensions.add("mlAppDeployer", newAppDeployer(manageClient, adminManager))
    }

    /**
     * Creates an AppDeployer with a default set of commands. A developer can then modify this in an
     * ext block.
     */
    AppDeployer newAppDeployer(ManageClient manageClient, AdminManager adminManager) {
        List<Command> commands = new ArrayList<Command>()
        
        // Security
        commands.add(new CreateRolesCommand())
        commands.add(new CreateUsersCommand())
        commands.add(new CreateAmpsCommand())
        commands.add(new CreateCertificateTemplatesCommand())
        commands.add(new CreateExternalSecurityCommand())
        commands.add(new CreatePrivilegesCommand())
        commands.add(new CreateProtectedCollectionsCommand())
        
        // Databases and appservers
        commands.add(new CreateRestApiServersCommand())
        commands.add(new CreateTriggersDatabaseCommand())
        commands.add(new CreateSchemasDatabaseCommand())
        commands.add(new CreateOtherServersCommand())
        commands.add(new UpdateContentDatabasesCommand())
        commands.add(new UpdateRestApiServersCommand())

        // Modules
        commands.add(new LoadModulesCommand())
        
        // CPF
        commands.add(new CreateCpfConfigsCommand())
        commands.add(new CreateDomainsCommand())
        commands.add(new CreatePipelinesCommand())
        
        // Others        
        commands.add(new CreateViewSchemasCommand())
        commands.add(new CreateScheduledTasksCommand())
        
        SimpleAppDeployer deployer = new SimpleAppDeployer(manageClient, adminManager)
        deployer.setCommands(commands)
        return deployer
    }
}
