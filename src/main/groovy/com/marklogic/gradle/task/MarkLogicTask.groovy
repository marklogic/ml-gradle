package com.marklogic.gradle.task

import org.gradle.api.DefaultTask

import com.marklogic.client.DatabaseClient
import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandContext
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.admin.AdminManager

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

    String getAdminUsername() {
        project.hasProperty("mlAdminUsername") ? project.property("mlAdminUsername") : project.property("mlUsername")
    }

	String getAdminPassword() {
		project.hasProperty("mlAdminPassword") ? project.property("mlAdminPassword") : project.property("mlPassword")
	}

	/**
	 * If the "database" property is set, then the DatabaseClient that's returned will use the App-Services port
	 * (defaults to 8000) to connect to the given database. Otherwise, the DatabaseClient will try to connect to the
	 * REST API server defined by mlRestPort.
	 * @return
	 */
	DatabaseClient newClient() {
		if (project.hasProperty("database")) {
			println "Connecting via the App-Services port to database: " + project.property("database")
			return getAppConfig().newAppServicesDatabaseClient(project.property("database"))
		}
		else {
			getAppConfig().newDatabaseClient()
		}
    }

    void deployWithCommandListProperty(String propertyName) {
        deployWithCommands(getProject().property(propertyName))
    }

    void deployWithCommands(List<Command> commands) {
        SimpleAppDeployer deployer = new SimpleAppDeployer(getManageClient(), getAdminManager())
        deployer.setCommands(commands)
        deployer.deploy(getAppConfig())
    }

    void undeployWithCommandListProperty(String propertyName) {
        undeployWithCommands(getProject().property(propertyName))
    }

    void undeployWithCommands(List<Command> commands) {
        SimpleAppDeployer deployer = new SimpleAppDeployer(getManageClient(), getAdminManager())
        deployer.setCommands(commands)
        deployer.undeploy(getAppConfig())
    }

    void invokeDeployerCommandWithClassName(String className) {
        SimpleAppDeployer d = (SimpleAppDeployer)getAppDeployer()
        new SimpleAppDeployer(getManageClient(), getAdminManager(), d.getCommand(className)).deploy(getAppConfig())
    }

    void undeployWithCommandWithClassName(String className) {
        SimpleAppDeployer d = (SimpleAppDeployer)getAppDeployer()
        new SimpleAppDeployer(getManageClient(), getAdminManager(), d.getCommand(className)).undeploy(getAppConfig())
    }
}
