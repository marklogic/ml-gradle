package com.marklogic.gradle.task.manage

import groovyx.net.http.HttpResponseDecorator

import org.gradle.api.Project

import com.marklogic.gradle.RestHelper
import com.marklogic.gradle.task.MarkLogicTask;

class ManageTask extends MarkLogicTask {

    // Used to check for existence of the XDBC server
    String groupId = "Default"

    ManageConfig getManageConfig() {
        getProject().property("mlManageConfig")
    }

    RestHelper newRestHelper() {
        ManageConfig config = getManageConfig()
        RestHelper h = new RestHelper()
        h.setUrl("http://" + config.getHost() + ":" + config.getPort())
        h.setUsername(config.getUsername())
        h.setPassword(config.getPassword())
        return h
    }

    HttpResponseDecorator invoke(Project project, String method, String path, String body, String requestContentType) {
        return newRestHelper().invoke(method, path, body, requestContentType)
    }

    HttpResponseDecorator invoke(Project project, String method, String path) {
        return newRestHelper().invoke(method, path)
    }
    
    boolean xdbcServerExists() {
        try {
            println "Checking to see if XDBC server exists..."
            invoke(getProject(), "GET", "/manage/v2/servers/" + getAppConfig().getName() + "-content-xdbc?group-id=" + groupId)
            return true
        } catch (Exception e) {
            return false
        }
    }
}