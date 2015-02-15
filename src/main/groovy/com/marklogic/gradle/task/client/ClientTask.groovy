package com.marklogic.gradle.task.client

import groovyx.net.http.HttpResponseDecorator

import org.gradle.api.DefaultTask
import org.gradle.api.Project

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.gradle.AppConfig;
import com.marklogic.gradle.RestHelper
import com.marklogic.gradle.task.MarkLogicTask;
import com.marklogic.gradle.task.manage.ManageConfig;

/**
 * Base class for any task that wishes to use a MarkLogic DatabaseClient. Expects to find configuration in the AppConfig
 * object that the MarkLogicPlugin adds to the set of project properties. Will default to using the mlUsername and 
 * mlPassword project properties if username/password are not set on AppConfig. 
 */
class ClientTask extends MarkLogicTask {

    Authentication auth = Authentication.DIGEST
    
    DatabaseClient newClient() {
        AppConfig config = getAppConfig()
        return DatabaseClientFactory.newClient(config.host, config.restPort, config.username, config.password, auth)
    }
    
    RestHelper newRestHelper() {
        AppConfig config = getAppConfig()
        RestHelper h = new RestHelper()
        h.setUrl("http://" + config.getHost() + ":" + config.getRestPort())
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
}