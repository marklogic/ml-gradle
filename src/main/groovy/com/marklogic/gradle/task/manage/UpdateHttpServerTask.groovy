package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.RestHelper;

class UpdateHttpServerTask extends ManageTask {

    String groupName = "Default"
    String format = "json"

    @TaskAction
    void addHttpServer() {
        String appName = getAppConfig().getName()        
        String packageName = appName + "-package"

        String path = getManageConfig().getHttpServerFilePath()
        println "Updating HTTP servers based on HTTP server package at " + path
        
        RestHelper rh = newRestHelper()
        rh.addHttpServerFromFile(packageName, groupName, appName, getAppConfig().getRestPort(), path)
        if (isTestPortSet()) {
            rh.addHttpServerFromFile(packageName, groupName, appName + "-test", getAppConfig().getTestRestPort(), path)
        }
        rh.installPackage(packageName, format)
        
        println "Finished updating HTTP servers in package " + packageName + "\n"
    }
}
