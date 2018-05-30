package com.marklogic.gradle.task.forests

import com.marklogic.appdeployer.AppConfig
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand
import com.marklogic.appdeployer.command.databases.DeployDatabaseCommand
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.api.forest.Forest
import org.gradle.api.tasks.TaskAction

class PrintForestPlanTask extends MarkLogicTask {

	@TaskAction
	void printForestPlan() {
		if (!project.hasProperty("database")) {
			println "Please specify a database via the 'database' property"
			return
		}

		String database = project.property("database")

		/**
		 * We unfortunately have to do a little work here to determine what command object to use based on the database
		 * name. That's to account for the "special" stuff that's done for the content database (mlContentForestsPerHost)
		 * and for the schema/triggers databases (those commands default to only having forests on one host).
		 */
		AppConfig appConfig = getAppConfig()
		SimpleAppDeployer appDeployer = getAppDeployer()
		DeployDatabaseCommand command
		if (database.equals(appConfig.getContentDatabaseName()) || database.equals(appConfig.getTestContentDatabaseName())) {
			command = appDeployer.getCommandOfType(DeployContentDatabasesCommand.class)
		} else if (database.equals(appConfig.getSchemasDatabaseName())) {
			command = appDeployer.getCommandOfType(DeploySchemasDatabaseCommand.class)
		} else if (database.equals(appConfig.getTriggersDatabaseName())) {
			command = appDeployer.getCommandOfType(DeployTriggersDatabaseCommand.class)
		} else {
			command = new DeployDatabaseCommand()
		}

		/**
		 * Now that we have a command, use it to build its command for deploying forests, and then use that to build
		 * the list of forests that will be created.
		 */
		List<Forest> forests = command.buildDeployForestsCommand(database, getCommandContext()).buildForests(getCommandContext(), true)


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
