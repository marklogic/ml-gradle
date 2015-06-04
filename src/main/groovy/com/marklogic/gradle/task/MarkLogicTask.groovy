package com.marklogic.gradle.task

import org.gradle.api.DefaultTask

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.AppPlugin
import com.marklogic.appdeployer.AppPluginContext
import com.marklogic.appdeployer.ConfigDir
import com.marklogic.appdeployer.SimpleAppDeployer
import com.marklogic.appdeployer.plugin.databases.CreateTriggersDatabasePlugin
import com.marklogic.appdeployer.plugin.databases.UpdateContentDatabasesPlugin
import com.marklogic.appdeployer.plugin.restapis.CreateRestApiServersPlugin
import com.marklogic.client.DatabaseClient
import com.marklogic.client.DatabaseClientFactory
import com.marklogic.rest.mgmt.ManageClient
import com.marklogic.rest.mgmt.admin.AdminManager

/**
 * Base class that provides easy access to all of the resources setup by MarkLogicPlugin.
 */
class MarkLogicTask extends DefaultTask {

    AppConfig getAppConfig() {
        getProject().property("mlAppConfig")
    }

    AppPluginContext getAppPluginContext() {
        getProject().property("mlAppPluginContext")
    }

    ConfigDir getConfigDir() {
        getProject().property("mlConfigDir")
    }

    /**
     * Look for an instance of AppDeployer in the project. In addition to avoiding creating an AppDeployer many times,
     * this also provides a way for a client to override the implementation. 
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
        List<AppPlugin> plugins = new ArrayList<AppPlugin>()
        plugins.add(new CreateRestApiServersPlugin())
        plugins.add(new UpdateContentDatabasesPlugin())
        plugins.add(new CreateTriggersDatabasePlugin())

        ManageClient manageClient = getProject().property("mlManageClient")
        AdminManager adminManager = getProject().property("mlAdminManager")
        SimpleAppDeployer deployer = new SimpleAppDeployer(manageClient, adminManager)
        deployer.setAppPlugins(plugins)
        return deployer
    }

    String getDefaultXccUrl() {
        getAppConfig().getXccUrl()
    }

    DatabaseClient newClient() {
        AppConfig config = getAppConfig()
        return DatabaseClientFactory.newClient(config.host, config.restPort, config.username, config.password, config.authentication)
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
