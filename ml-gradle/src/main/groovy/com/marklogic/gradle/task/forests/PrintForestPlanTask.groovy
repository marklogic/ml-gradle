/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.forests


import com.marklogic.appdeployer.command.forests.ForestPlanner
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.api.forest.Forest
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
		List<Forest> forests = new ForestPlanner(getManageClient()).previewForestPlan(database, getAppConfig())

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
