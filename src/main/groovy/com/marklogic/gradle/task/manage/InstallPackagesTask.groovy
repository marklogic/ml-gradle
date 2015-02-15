package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.AppConfig
import com.marklogic.gradle.RestHelper

/**
 * Eventually want most of this logic to move to Java classes that represent packages with different configurations.
 * This task should just collect task inputs to configure those Java classes, which will then handle installing an
 * application. DatabasePackageManager is the first step in moving code out.
 */
class InstallPackagesTask extends ManageTask {

    String groupName = "Default"
    String triggersDatabaseFilePath = "src/main/xqy/packages/triggers-database.xml"
    String schemasDatabaseFilePath = "src/main/xqy/packages/schemas-database.xml"
    String xdbcServerFilename = "src/main/xqy/packages/xdbc-server.xml"

    String format = "json"

    @TaskAction
    void installPackages() {
        AppConfig appConfig = getAppConfig()
        String appName = appConfig.getName()
        String packageName = appName + "-package"
        String restServerName = appName

        RestHelper rh = newRestHelper()

        rh.deletePackage(packageName)
        rh.createPackage(packageName, format)

        boolean installPackage = false;
        boolean installTestResources = isTestPortSet();

        if (new File(triggersDatabaseFilePath).exists()) {
            String triggersDatabaseName = appName + "-triggers"
            rh.addDatabase(packageName, triggersDatabaseName, triggersDatabaseFilePath, format)
            installPackage = true
        }

        if (new File(schemasDatabaseFilePath).exists()) {
            String schemasDatabaseName = appName + "-schemas"
            rh.addDatabase(packageName, schemasDatabaseName, schemasDatabaseFilePath, format)
            installPackage = true
        }

        DatabasePackageManager mgr = new DatabasePackageManager(appName)
        String contentDatabaseFilePath = getManageConfig().getContentDatabaseFilePath()
        if (new File(contentDatabaseFilePath).exists()) {
            println "Installing databases based on content database package at " + contentDatabaseFilePath
            mgr.addContentDatabasesToPackage(rh, contentDatabaseFilePath, installTestResources, format)
            installPackage = true
        } else {
            println "No content database package file found, so not installing a content database"
        }

        if (installPackage) {
            rh.installPackage(packageName, format)
        }

        rh.createRestApi(restServerName, mgr.getContentDatabaseName(), appConfig.getRestPort(), "")
        if (installTestResources) {
            String assumedModulesDatabase = appName + "-modules"
            rh.createRestApi(restServerName + "-test", mgr.getTestContentDatabaseName(), appConfig.getTestRestPort(), assumedModulesDatabase)
        }

        rh.addXdbcServer(packageName, groupName, appName, mgr.getContentDatabaseName(), appConfig.getXdbcPort(), xdbcServerFilename)
        if (installTestResources) {
            rh.addXdbcServer(packageName, groupName, appName, mgr.getTestContentDatabaseName(), appConfig.getTestXdbcPort(), xdbcServerFilename)
        }
        if (appConfig.getModulesXdbcPort() != null && appConfig.getModulesXdbcPort() > 0) {
            rh.addModulesXdbcServer(appName, appConfig.getModulesXdbcPort())
        }
        
        rh.installPackage(packageName, format)
        println "Finished installing application ${appName}\n"
    }
}
