package com.marklogic.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

import com.marklogic.gradle.task.DeleteModuleTimestampsFileTask
import com.marklogic.gradle.task.UninstallAppTask
import com.marklogic.gradle.task.client.LoadAssetsViaMlcpTask
import com.marklogic.gradle.task.client.LoadModulesTask
import com.marklogic.gradle.task.client.PrepareRestApiDependenciesTask
import com.marklogic.gradle.task.client.WatchTask
import com.marklogic.gradle.task.client.config.CreateTransformTask
import com.marklogic.gradle.task.client.service.CreateResourceTask
import com.marklogic.gradle.task.cpf.InsertAlertingPipelineTask
import com.marklogic.gradle.task.cpf.InsertSchPipelineTask
import com.marklogic.gradle.task.database.ClearContentDatabaseTask
import com.marklogic.gradle.task.database.ClearModulesTask
import com.marklogic.gradle.task.manage.ConfigureBitemporalTask
import com.marklogic.gradle.task.manage.InstallPackagesTask
import com.marklogic.gradle.task.manage.ManageConfig
import com.marklogic.gradle.task.manage.MergeDatabasePackagesTask
import com.marklogic.gradle.task.manage.MergeHttpServerPackagesTask
import com.marklogic.gradle.task.manage.UpdateDatabaseTask
import com.marklogic.gradle.task.manage.UpdateHttpServerTask

class MarkLogicPlugin implements Plugin<Project> {

    void apply(Project project) {
        initializeAppConfig(project)
        initializeManageConfig(project)

        project.getConfigurations().create("mlRestApi")

        String group = "MarkLogic"

        project.task("mlDeleteModuleTimestampsFile", type: DeleteModuleTimestampsFileTask, group: group, description: "Delete the properties file in the build directory that keeps track of when each module was last loaded.")
        project.task("mlUninstallApp", type: UninstallAppTask, group: group, description: "Delete all application resources; this currently has a bug that may require manually deleting the content forest")

        project.task("mlClearContentDatabase", type: ClearContentDatabaseTask, group: group, description: "Deletes all or a collection of documents from the content database")
        project.task("mlClearModules", type: ClearModulesTask, group: group, dependsOn: "mlDeleteModuleTimestampsFile", description: "Deletes potentially all of the documents in the modules database; has a property for excluding documents from deletion")

        project.task("mlPrepareRestApiDependencies", type: PrepareRestApiDependenciesTask, group: group, dependsOn: project.configurations["mlRestApi"], description: "Downloads (if necessary) and unzips in the build directory all mlRestApi dependencies")

        project.task("mlMergeDatabasePackages", type: MergeDatabasePackagesTask, group: group, dependsOn:"mlPrepareRestApiDependencies", description: "Merges together the database packages that are defined by a property on this task; the result is written to the build directory")
        project.task("mlMergeHttpServerPackages", type: MergeHttpServerPackagesTask, group: group, description:"Merges together the HTTP server packages that are defined by a property on this task; the result is written to the build directory")

        project.task("mlInstallPackages", type: InstallPackagesTask, group: group, dependsOn: [
            "mlMergeDatabasePackages",
            "mlMergeHttpServerPackages"
        ], description: "Installs the application's packages (servers and databases); does not load any modules").mustRunAfter("mlClearModules")

        project.task("mlPostInstallPackages", group: group, description: "Add dependsOn to this task to add tasks after mlInstallPackages finishes within mlDeploy").mustRunAfter("mlInstallPackages")

        project.task("mlLoadModules", type: LoadModulesTask, group: group, dependsOn: "mlPrepareRestApiDependencies", description: "Loads modules from directories defined by mlAppConfig or via a property on this task").mustRunAfter([
            "mlPostInstallPackages",
            "mlClearModules"
        ])

        project.task("mlPostDeploy", group: group, description: "Add dependsOn to this to add tasks to mlDeploy").mustRunAfter("mlLoadModules")

        project.task("mlDeploy", group: group, dependsOn: [
            "mlClearModules",
            "mlInstallPackages",
            "mlPostInstallPackages",
            "mlLoadModules",
            "mlPostDeploy"
        ], description: "Deploys the application by first clearing the modules database (if it exists), installing packages, and then loading modules")

        project.task("mlReloadModules", group: group, dependsOn: [
            "mlClearModules",
            "mlLoadModules"
        ], description: "Reloads modules by first clearing the modules database and then loading modules")

        project.task("mlUpdateContentDatabase", type: UpdateDatabaseTask, group: group, dependsOn: "mlMergeDatabasePackages", description: "Updates the content database by building a new database package and then installing it")
        project.task("mlUpdateHttpServers", type: UpdateHttpServerTask, group: group, dependsOn: "mlMergeHttpServerPackages", description: "Updates the HTTP servers by building a new HTTP server package and then installing it")

        project.task("mlCreateResource", type: CreateResourceTask, group: group, description: "Create a new resource extension in the src/main/xqy/services directory")
        project.task("mlCreateTransform", type: CreateTransformTask, group: group, description: "Create a new transform in the src/main/xqy/transforms directory")

        project.task("mlConfigureBitemporal", type: ConfigureBitemporalTask, group: group, description: "For MarkLogic 8 - configure support for bitemporal features")

        project.task("mlWatch", type: WatchTask, group: group, description: "Run a loop that checks for new/modified modules every second and loads any that it finds")

        // Tasks for loading asset modules via MLCP
        project.task("mlDeleteConsolidatedAssets", type: Delete, group: group) {
            description = "Delete the directory of consolidated asset modules"
            delete "build/ml-gradle/consolidatedAssets"
        }
        
        project.task("mlConsolidateAssets", type: Copy, group: group, dependsOn: ["mlDeleteConsolidatedAssets", "mlPrepareRestApiDependencies"]) {
            description = "Copy asset modules from each of the module paths in mlAppConfig to a temporarily build directory"
            doFirst {
                // Need to update this based on additions made by mlPrepareRestApiDependencies
                from(project.property("mlAppConfig").getModulePaths())
            }
            from(project.property("mlAppConfig").getModulePaths())
            into("build/ml-gradle/consolidatedAssets")
            include("ext/**")
        }
        
        project.task("mlLoadAssetsViaMlcp", type: LoadAssetsViaMlcpTask, group: group, dependsOn: "mlConsolidateAssets", description: "Loads consolidated assets via MLCP, which is much faster than loading each asset module via the REST API")
        
        // CPF tasks
        String cpfGroup = "MarkLogic CPF"
        project.task("mlInsertSchPipeline", type: InsertSchPipelineTask, group: cpfGroup, description: "Inserts the Status Change Handling pipeline")
        project.task("mlInsertAlertingPipeline", type: InsertAlertingPipelineTask, group: cpfGroup, description: "Inserts the Alerting pipeline")
    }

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

    void initializeManageConfig(Project project) {
        ManageConfig manageConfig = new ManageConfig()
        if (project.hasProperty("mlHost")) {
            def host = project.property("mlHost")
            println "Manage app host: " + host
            manageConfig.setHost(host)
        }
        if (project.hasProperty("mlUsername")) {
            def username = project.property("mlUsername")
            println "Manage app username: " + username
            manageConfig.setUsername(username)
        }
        if (project.hasProperty("mlPassword")) {
            manageConfig.setPassword(project.property("mlPassword"))
        }
        project.extensions.add("mlManageConfig", manageConfig)
    }
}
