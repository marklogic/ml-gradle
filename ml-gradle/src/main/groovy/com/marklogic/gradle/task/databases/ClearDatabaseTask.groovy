/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import com.marklogic.gradle.task.AbstractConfirmableTask
import com.marklogic.mgmt.resource.databases.DatabaseManager
import org.gradle.api.GradleException

class ClearDatabaseTask extends AbstractConfirmableTask {

	@Override
	void executeIfConfirmed() {
		if (project.hasProperty("database")) {
			String db = project.property("database")
			println "Clearing database: " + db
			new DatabaseManager(getManageClient()).clearDatabase(db)
			println "Finished clearing database: " + db
		} else {
			throw new GradleException("The property 'database' must be specified")
		}
	}
}
