/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import com.marklogic.gradle.task.AbstractConfirmableTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class ClearContentDatabaseTask extends AbstractConfirmableTask {

	@Override
	void executeIfConfirmed() {
		println "Clearing content database"
		DatabaseManager mgr = new DatabaseManager(getManageClient())
		mgr.clearDatabase(getAppConfig().getContentDatabaseName())
		println "Finished clearing content database"
	}

}
