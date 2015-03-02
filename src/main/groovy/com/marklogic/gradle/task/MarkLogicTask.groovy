package com.marklogic.gradle.task

import org.gradle.api.DefaultTask

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.ml7.Ml7AppDeployer
import com.marklogic.appdeployer.ml7.Ml7ManageClient
import com.marklogic.client.DatabaseClient
import com.marklogic.client.DatabaseClientFactory
import com.marklogic.client.DatabaseClientFactory.Authentication
import com.marklogic.gradle.task.manage.ManageConfig

class MarkLogicTask extends DefaultTask {

    AppConfig getAppConfig() {
        getProject().property("mlAppConfig")
    }

    /**
     * Look for an instance of AppDeployer in the project. In addition to avoiding creating an AppDeployer many times,
     * I think this also provides a way for a client to override the implementation. 
     */
    AppDeployer getAppDeployer() {
        String propName = "mlAppDeployer"
        if (getProject().hasProperty(propName)) {
            return getProject().property(propName)
        }
        AppDeployer d = newAppDeployer()
        getProject().getExtensions().add(propName, d)
        return d
    }

    AppDeployer newAppDeployer() {
        ManageConfig config = getProject().property("mlManageConfig")
        Ml7ManageClient client = new Ml7ManageClient(config.getHost(), config.getPort(), config.getUsername(), config.getPassword())
        return new Ml7AppDeployer(client);
    }

    String getDefaultXccUrl() {
        getAppConfig().getXccUrl()
    }

    DatabaseClient newClient() {
        AppConfig config = getAppConfig()
        return DatabaseClientFactory.newClient(config.host, config.restPort, config.username, config.password, Authentication.DIGEST)
    }

    RestHelper newRestHelper() {
        AppConfig config = getAppConfig()
        RestHelper h = new RestHelper()
        h.setUrl("http://" + config.getHost() + ":" + config.getRestPort())
        h.setUsername(config.getUsername())
        h.setPassword(config.getPassword())
        return h
    }
}
