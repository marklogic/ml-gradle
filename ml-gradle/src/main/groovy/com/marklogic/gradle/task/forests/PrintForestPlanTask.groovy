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
package com.marklogic.gradle.task.forests

import com.marklogic.appdeployer.command.databases.DatabasePlan
import com.marklogic.appdeployer.command.databases.DeployDatabaseCommand
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand
import com.marklogic.appdeployer.command.forests.DeployForestsCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.api.forest.Forest
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * This is only intended for use when creating forests via properties. The goal is to look at the properties defined by
 * a user and preview what forests will be created based on those properties. If a user is explicitly defining forests
 * via payloads instead of properties, the expectation is that a user can simply look at what forests already exist and
 * compare those to the files in their project.
 */
class PrintForestPlanTask extends MarkLogicTask {

	@TaskAction
	void printForestPlan() {
		if (!project.hasProperty("database")) {
			println "Please specify a database via the 'database' property"
			return
		}

		String database = project.property("database")

		SimpleAppDeployer appDeployer = getAppDeployer()
		DeployOtherDatabasesCommand command = appDeployer.getCommandOfType(DeployOtherDatabasesCommand.class)
		List<DatabasePlan> plans = command.buildDatabasePlans(getCommandContext())

		DeployDatabaseCommand deployDatabaseCommand
		for (DatabasePlan plan : plans) {
			if (database.equals(plan.getDatabaseName())) {
				deployDatabaseCommand = plan.getDeployDatabaseCommand()
				break
			}
		}

		if (deployDatabaseCommand == null) {
			throw new GradleException("Did not find any database plan with a database name of: " + database)
		}

		DeployForestsCommand deployForestsCommand = deployDatabaseCommand.buildDeployForestsCommand(database, getCommandContext())
		List<Forest> forests = deployForestsCommand != null ?
			deployForestsCommand.buildForests(getCommandContext(), true) :
			new ArrayList<>()

		if (forests.isEmpty()) {
			println "\nNo primary forests will be created the next time the database '" + database + "' is deployed. This is " +
				"likely because it already has all of the primary desired forests based on the configuration settings, or because " +
				"you are explicitly defining forests to create instead of creating forests dynamically via properties."
			println "\nIf replicas have been configured for the database - e.g. via mlDatabaseNamesAndReplicaCounts - and these do not exist yet, " +
				"then replicas will be created the next time either the mlDeploy task or mlConfigureForestReplicas task is run."
		} else {
			for (Forest f : forests) {
				println f.getJson()
			}
			println "\nThe " + forests.size() + " forests (and replicas if applicable) that will be created the next time the database '" + database + "' is deployed (e.g. via the mlDeploy task) are listed above."
		}
	}
}
