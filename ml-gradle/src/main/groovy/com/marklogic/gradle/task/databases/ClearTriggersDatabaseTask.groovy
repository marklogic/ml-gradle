/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class ClearTriggersDatabaseTask extends MarkLogicTask {

	@TaskAction
	void clearModules() {
		println "\nConsider using mlClearDatabase instead, which provides more flexibility via -Pdatabase= for clearing any database"

		println "\nClearing all documents in triggers database"
		DatabaseManager mgr = new DatabaseManager(getManageClient())
		mgr.clearDatabase(getAppConfig().getTriggersDatabaseName())
		println "Finished clearing all documents in triggers database"
	}
}
