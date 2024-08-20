/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle.task

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandContext
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.client.DatabaseClient
import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.admin.AdminManager
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal

/**
 * Base class that provides easy access to all of the resources setup by MarkLogicPlugin.
 */
class MarkLogicTask extends DefaultTask {

	@Internal
    AppConfig getAppConfig() {
        getProject().property("mlAppConfig")
    }

	@Internal
    CommandContext getCommandContext() {
        getProject().property("mlCommandContext")
    }

	@Internal
    ManageClient getManageClient() {
        getProject().property("mlManageClient")
    }

	@Internal
    AppDeployer getAppDeployer() {
        getProject().property("mlAppDeployer")
    }

	@Internal
    AdminManager getAdminManager() {
        getProject().property("mlAdminManager")
    }

	// TODO Should remove this as "mlSecurityUsername" is preferred, and this is now specific to InstallAdminTask
	@Deprecated
	@Internal
    String getAdminUsername() {
        project.hasProperty("mlAdminUsername") ? project.property("mlAdminUsername") : project.property("mlUsername")
    }

	// TODO Should remove this as "mlSecurityUsername" is preferred, and this is now specific to InstallAdminTask
	@Deprecated
	@Internal
	String getAdminPassword() {
		project.hasProperty("mlAdminPassword") ? project.property("mlAdminPassword") : project.property("mlPassword")
	}

	DatabaseClient newClient() {
		newClient(null)
    }

	/**
	 * If the "database" property is set or is passed as a parameter, then the DatabaseClient that's returned will use
	 * the App-Services port (defaults to 8000) to connect to the given database. Otherwise, the DatabaseClient will
	 * try to connect to the REST API server defined by mlRestPort.
	 * @return
	 */
	DatabaseClient newClient(String database) {
		if (database != null){
			println "Connecting via the App-Services port to database: " + database
			return getAppConfig().newAppServicesDatabaseClient(database)
		}
		else if (project.hasProperty("database")) {
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
        newAppDeployer(commands).deploy(getAppConfig())
    }

    void undeployWithCommandListProperty(String propertyName) {
        undeployWithCommands(getProject().property(propertyName))
    }

    void undeployWithCommands(List<Command> commands) {
        newAppDeployer(commands).undeploy(getAppConfig())
    }

	AppDeployer newAppDeployer(List<Command> commands) {
		SimpleAppDeployer deployer = new SimpleAppDeployer(getManageClient(), getAdminManager())
		deployer.setCommands(commands)
		return deployer
	}

	Command getCommandWithClassName(String className) {
		SimpleAppDeployer d = (SimpleAppDeployer)getAppDeployer()
		Command command = d.getCommand(className)
		// New in 4.4.0 - before, null was returned and no command was run, which would be very unexpected when the
		// caller is asking to run a specific command.
		if (command == null) {
			throw new GradleException("No command found with class name: " + className)
		}
		return command
	}

    void invokeDeployerCommandWithClassName(String className) {
        new SimpleAppDeployer(getManageClient(), getAdminManager(), getCommandWithClassName(className)).deploy(getAppConfig())
    }

    void undeployWithCommandWithClassName(String className) {
        new SimpleAppDeployer(getManageClient(), getAdminManager(), getCommandWithClassName(className)).undeploy(getAppConfig())
    }
}
