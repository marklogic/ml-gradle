package com.rjrudin.marklogic.gradle.task

import org.gradle.api.DefaultTask

import com.marklogic.client.DatabaseClient
import com.rjrudin.marklogic.appdeployer.AppConfig
import com.rjrudin.marklogic.appdeployer.AppDeployer
import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.appdeployer.command.CommandContext
import com.rjrudin.marklogic.appdeployer.impl.SimpleAppDeployer
import com.rjrudin.marklogic.mgmt.ManageClient
import com.rjrudin.marklogic.mgmt.admin.AdminManager

/**
 * Base class that provides easy access to all of the resources setup by MarkLogicPlugin.
 */
class MarkLogicTask extends DefaultTask {

    AppConfig getAppConfig() {
        getProject().property("mlAppConfig")
    }

    CommandContext getCommandContext() {
        getProject().property("mlCommandContext")
    }

    ManageClient getManageClient() {
        getProject().property("mlManageClient")
    }

    AppDeployer getAppDeployer() {
        getProject().property("mlAppDeployer")
    }

    AdminManager getAdminManager() {
        getProject().property("mlAdminManager")
    }

    DatabaseClient newClient() {
        getAppConfig().newDatabaseClient()
    }
    
    void deployWithCommandListProperty(String propertyName) {
        deployWithCommands(getProject().property(propertyName))
    }

    void deployWithCommands(List<Command> commands) {
        SimpleAppDeployer deployer = new SimpleAppDeployer(getManageClient(), getAdminManager())
        deployer.setCommands(commands)
        deployer.deploy(getAppConfig())
    }
    
    void invokeDeployerCommandWithClassName(String className) {
        SimpleAppDeployer d = (SimpleAppDeployer)getAppDeployer()
        new SimpleAppDeployer(getManageClient(), getAdminManager(), d.getCommand(className)).deploy(getAppConfig())
    }
}
