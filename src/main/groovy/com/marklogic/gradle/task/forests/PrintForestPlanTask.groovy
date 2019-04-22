package com.marklogic.gradle.task.forests

import com.marklogic.appdeployer.command.databases.DatabasePlan
import com.marklogic.appdeployer.command.databases.DeployDatabaseCommand
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.api.forest.Forest
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

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

		/**
		 * Now that we have a command, use it to build its command for deploying forests, and then use that to build
		 * the list of forests that will be created.
		 */
		List<Forest> forests = deployDatabaseCommand.buildDeployForestsCommand(
			database, getCommandContext()).buildForests(getCommandContext(), true)


		if (forests.isEmpty()) {
			println "\nNo primary forests will be created the next time the database '" + database + "' is deployed; this is likely because it already has all of the primary desired forests based on the configuration settings."
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
