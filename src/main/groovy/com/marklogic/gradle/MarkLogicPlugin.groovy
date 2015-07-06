package com.marklogic.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.ConfigDir
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandContext
import com.marklogic.appdeployer.command.cpf.CreateCpfConfigsCommand
import com.marklogic.appdeployer.command.cpf.CreateDomainsCommand
import com.marklogic.appdeployer.command.cpf.CreatePipelinesCommand
import com.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand
import com.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand
import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand
import com.marklogic.appdeployer.command.security.CreateAmpsCommand
import com.marklogic.appdeployer.command.security.CreateCertificateTemplatesCommand
import com.marklogic.appdeployer.command.security.CreateExternalSecurityCommand
import com.marklogic.appdeployer.command.security.CreatePrivilegesCommand
import com.marklogic.appdeployer.command.security.CreateRolesCommand
import com.marklogic.appdeployer.command.security.CreateUsersCommand
import com.marklogic.appdeployer.command.servers.UpdateRestApiServersCommand
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
import com.marklogic.gradle.task.databases.ClearModulesTask
import com.marklogic.gradle.task.databases.UpdateContentDatabasesTask
import com.marklogic.gradle.task.scaffold.GenerateScaffoldTask
import com.marklogic.gradle.task.servers.UpdateRestApiServersTask
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

        String group = "ml-gradle"

        // admin/v1 tasks
        project.task("mlInit", type: InitTask, group: group, description: "Perform a one-time initialization of a MarkLogic server")
        project.task("mlInstallAdmin", type: InstallAdminTask, group: group, description: "Perform a one-time installation of an admin user")

        project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: group, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded.")
        project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: group, description: "Deletes all or a collection of documents from the content database")
        project.task("mlClearModules", type: ClearModulesTask, group: group, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")
        project.task("mlPrepareRestApiDependencies", type: PrepareRestApiDependenciesTask, group: group, dependsOn: project.configurations["mlRestApi"], description: "Downloads (if necessary) and unzips in the build directory all mlRestApi dependencies")

        /**
         * Tasks for deploying and undeploying. mlDeploy and mlUndeploy exist so that a developer can easily use
         * dependsOn and mustRunAfter to add additional steps after an application has been deployed/undeployed.
         */
        project.task("mlAppDeploy", type: DeployAppTask, group: group, dependsOn: ["mlDeleteModuleTimestampsFile", "mlPrepareRestApiDependencies"], description: "Deploys the application")
        project.task("mlAppUndeploy", type: UndeployAppTask, group: group, description: "Undeploys the application")
        project.task("mlPostDeploy", group: group, description: "Called by mlDeploy after mlAppDeploy as a way of allowing tasks to easily be added to mlDeploy").mustRunAfter(["mlAppDeploy"])
        project.task("mlDeploy", group: group, dependsOn: ["mlAppDeploy", "mlPostDeploy"], description: "Deploys the application and allows for additional steps via dependsOn")
        project.task("mlPostUndeploy", group: group, description: "Called by mlUndeploy after mlAppUndeploy as a way of allowing tasks to easily be added to mlUndeploy").mustRunAfter(["mlAppUndeploy"])
        project.task("mlUndeploy", group: group, dependsOn: ["mlAppUndeploy", "mlPostUndeploy"], description: "Undeploys the application and allows for additional steps via dependsOn")

        // Tasks for loading modules
        project.task("mlLoadModules", type: LoadModulesTask, group: group, dependsOn: "mlPrepareRestApiDependencies", description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter(["mlClearModules"])
        project.task("mlReloadModules", group: group, dependsOn: ["mlClearModules", "mlLoadModules"], description: "Reloads modules by first clearing the modules database and then loading modules")
        project.task("mlWatch", type: WatchTask, group: group, description: "Run a loop that checks for new/modified modules every second and loads any that it finds")

        // Tasks for configuring specific parts of an application
        project.task("mlUpdateContentDatabase", type: UpdateContentDatabasesTask, group: group, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the content databases")
        project.task("mlUpdateRestApiServers", type: UpdateRestApiServersTask, group: group, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the REST API servers")

        // CPF tasks
        project.task("mlCpfDeploy", type: DeployCpfTask, group: group, description: "Deploy CPF pipelines, domains, and configurations")
        project.task("mlCpfLoadDefaultPipelines", type: LoadDefaultPipelinesTask, group: group, description: "Load default pipelines into a triggers database")
        
        // Tasks for generating code
        project.task("mlScaffold", type: GenerateScaffoldTask, group: group, description: "Generate project scaffold for a new project")
        project.task("mlCreateResource", type: CreateResourceTask, group: group, description: "Create a new resource extension in the src/main/xqy/services directory")
        project.task("mlCreateTransform", type: CreateTransformTask, group: group, description: "Create a new transform in the src/main/xqy/transforms directory")
        
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
        commands.add(new CreateRolesCommand())
        commands.add(new CreateUsersCommand())
        commands.add(new CreateAmpsCommand())
        commands.add(new CreateCertificateTemplatesCommand())
        commands.add(new CreateExternalSecurityCommand())
        commands.add(new CreatePrivilegesCommand())
        commands.add(new CreateRestApiServersCommand())
        commands.add(new UpdateContentDatabasesCommand())
        commands.add(new CreateTriggersDatabaseCommand())
        commands.add(new LoadModulesCommand())
        commands.add(new UpdateRestApiServersCommand())
        commands.add(new CreateCpfConfigsCommand())
        commands.add(new CreateDomainsCommand())
        commands.add(new CreatePipelinesCommand())

        SimpleAppDeployer deployer = new SimpleAppDeployer(manageClient, adminManager)
        deployer.setCommands(commands)
        return deployer
    }
}
