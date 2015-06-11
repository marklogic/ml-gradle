package com.marklogic.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.CommandContext
import com.marklogic.gradle.task.DeleteModuleTimestampsFileTask
import com.marklogic.gradle.task.DeployAppTask
import com.marklogic.gradle.task.UndeployAppTask
import com.marklogic.gradle.task.client.CreateResourceTask
import com.marklogic.gradle.task.client.CreateTransformTask
import com.marklogic.gradle.task.client.LoadModulesTask
import com.marklogic.gradle.task.client.PrepareRestApiDependenciesTask
import com.marklogic.gradle.task.client.WatchTask
import com.marklogic.gradle.task.databases.ClearContentDatabaseTask
import com.marklogic.gradle.task.databases.ClearModulesTask
import com.marklogic.gradle.task.databases.UpdateContentDatabasesTask
import com.marklogic.gradle.task.servers.UpdateRestApiServersTask
import com.marklogic.rest.mgmt.ManageClient
import com.marklogic.rest.mgmt.ManageConfig
import com.marklogic.rest.mgmt.admin.AdminConfig
import com.marklogic.rest.mgmt.admin.AdminManager

class MarkLogicPlugin implements Plugin<Project> {

    void apply(Project project) {
        initializeAppConfig(project)
        initializeManageConfig(project)
        initializeAdminConfig(project)
        initializeAppDeployerObjects(project)

        project.getConfigurations().create("mlRestApi")

        String group = "ml-gradle"

        project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: group, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded.")

        project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: group, description: "Deletes all or a collection of documents from the content database")
        project.task("mlClearModules", type: ClearModulesTask, group: group, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")

        project.task("mlPrepareRestApiDependencies", type: PrepareRestApiDependenciesTask, group: group, dependsOn: project.configurations["mlRestApi"], description: "Downloads (if necessary) and unzips in the build directory all mlRestApi dependencies")

        /**
         * Tasks for deploying and undeploying. mlDeploy and mlUndeploy exist so that a developer can easily use
         * dependsOn and mustRunAfter to add additional steps after an application has been deployed/undeployed.
         */
        project.task("mlAppDeploy", type: DeployAppTask, group: group, description: "Deploys the application")
        project.task("mlAppUndeploy", type: UndeployAppTask, group: group, description: "Undeploys the application")
        project.task("mlDeploy", group: group, dependsOn:["mlAppDeploy"], description: "Deploys the application and allows for additional steps via dependsOn")
        project.task("mlUndeploy", group: group, dependsOn:["mlAppUndeploy"], description: "Undeploys the application and allows for additional steps via dependsOn")

        // Tasks for loading modules
        project.task("mlLoadModules", type: LoadModulesTask, group: group, dependsOn: "mlPrepareRestApiDependencies", description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter(["mlClearModules"])
        project.task("mlReloadModules", group: group, dependsOn: ["mlClearModules", "mlLoadModules"], description: "Reloads modules by first clearing the modules database and then loading modules")
        project.task("mlWatch", type: WatchTask, group: group, description: "Run a loop that checks for new/modified modules every second and loads any that it finds")

        // Tasks for configuring specific parts of an application
        project.task("mlUpdateContentDatabase", type: UpdateContentDatabasesTask, group: group, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the content databases")
        project.task("mlUpdateRestApiServers", type: UpdateRestApiServersTask, group: group, dependsOn: "mlPrepareRestApiDependencies", description: "Updates the REST API servers")

        // Tasks for generating code
        project.task("mlCreateResource", type: CreateResourceTask, group: group, description: "Create a new resource extension in the src/main/xqy/services directory")
        project.task("mlCreateTransform", type: CreateTransformTask, group: group, description: "Create a new transform in the src/main/xqy/transforms directory")
    }

    /**
     * Read in certain project properties and use them to initialize an instance of AppConfig. The properties are typically
     * defined in gradle.properties.
     * 
     * @param project
     */
    void initializeAppConfig(Project project) {
        AppConfig appConfig = new AppConfig()

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
            appConfig.setUsername(username)
        }
        if (project.hasProperty("mlPassword")) {
            appConfig.setPassword(project.property("mlPassword"))
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

        if (project.hasProperty("mlXdbcPort")) {
            def port = project.property("mlXdbcPort")
            println "App XDBC port: " + port
            appConfig.setXdbcPort(Integer.parseInt(port))
        }
        if (project.hasProperty("mlTestXdbcPort")) {
            def port = project.property("mlTestXdbcPort")
            println "App test XDBC port: " + port
            appConfig.setTestXdbcPort(Integer.parseInt(port))
        }
        if (project.hasProperty("mlModulesXdbcPort")) {
            def port = project.property("mlModulesXdbcPort")
            println "App modules XDBC port: " + port
            appConfig.setModulesXdbcPort(Integer.parseInt(port))
        }

        project.extensions.add("mlAppConfig", appConfig)
    }

    /**
     * TODO Should allow for this to be initialized off a different set of properties if they
     * exist - e.g. mlManageUsername.
     * 
     * @param project
     */
    void initializeManageConfig(Project project) {
        ManageConfig manageConfig = new ManageConfig()
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            println "Manage host: " + host
            manageConfig.setHost(host)
        }
        if (project.hasProperty("mlUsername")) {
            def username = project.property("mlUsername")
            println "Manage username: " + username
            manageConfig.setUsername(username)
        }
        if (project.hasProperty("mlPassword")) {
            manageConfig.setPassword(project.property("mlPassword"))
        }
        project.extensions.add("mlManageConfig", manageConfig)
    }

    /**
     * TODO Should allow for this to be initialized off a different set of properties if they
     * exist - e.g. mlAdminUsername.
     * 
     * @param project
     */
    void initializeAdminConfig(Project project) {
        AdminConfig adminConfig = new AdminConfig()
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            println "Admin host: " + host
            adminConfig.setHost(host)
        }
        if (project.hasProperty("mlUsername")) {
            def username = project.property("mlUsername")
            println "Admin username: " + username
            adminConfig.setUsername(username)
        }
        if (project.hasProperty("mlPassword")) {
            adminConfig.setPassword(project.property("mlPassword"))
        }
        project.extensions.add("mlAdminConfig", adminConfig)
    }


    /**
     * So we won't initialize an AppDeployer here... MarkLogicTask can fall back to a default impl, perhaps
     * one that uses the DefaultConfiguration. But a developer could add one in the ext block which 
     * MarkLogicTask would then use.
     * 
     * @param project
     */
    void initializeAppDeployerObjects(Project project) {
        ManageConfig manageConfig = project.extensions.getByName("mlManageConfig")

        ManageClient manageClient = new ManageClient(manageConfig)
        project.extensions.add("mlManageClient", manageClient)

        AdminManager adminManager = new AdminManager(project.extensions.getByName("mlAdminConfig"))
        project.extensions.add("mlAdminManager", adminManager)

        CommandContext context = new CommandContext(project.extensions.getByName("mlAppConfig"), manageClient, adminManager)
        project.extensions.add("mlCommandContext", context)
    }
}
