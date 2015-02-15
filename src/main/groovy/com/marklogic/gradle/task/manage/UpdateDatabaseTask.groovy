package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.RestHelper

class UpdateDatabaseTask extends ManageTask {

    String format = "json"
    
    @TaskAction
    void updateDatabase() {
        String appName = getAppConfig().getName()
        DatabasePackageManager mgr = new DatabasePackageManager(appName)
        
        String path = getManageConfig().getContentDatabaseFilePath()
        println "Updating databases based on content database package at " + path
        RestHelper rh = newRestHelper()
        List<String> names = mgr.addContentDatabasesToPackage(rh, path, isTestPortSet(), format)
        rh.installPackage(mgr.getPackageName(), format)
        println "Finished updating database " + names + " in package " + mgr.getPackageName() + "\n"
    }
}
