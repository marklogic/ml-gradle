package com.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils

import com.marklogic.gradle.xcc.XccHelper

class UninstallAppTask extends MarkLogicTask {

    String xccUrl

    @TaskAction
    void uninstallApp() {
        if (!xccUrl) {
            xccUrl = getDefaultXccUrl()
        }

        String xquery = new String(FileCopyUtils.copyToByteArray(new ClassPathResource("ml-gradle/uninstall-app.xqy").getInputStream()))
        String appName = getAppConfig().getName()
        xquery = xquery.replace("%%APP_NAME%%", appName)
        println "Uninstalling app with name " + appName
        try {
            println new XccHelper(xccUrl).executeXquery(xquery)
        } catch (Exception e) {
            println "Unable to uninstall app, cause: " + e.getMessage()
        }
    }
}
