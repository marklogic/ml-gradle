/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.admin

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class InstallAdminTask extends MarkLogicTask {

	@Input
	@Optional
	String realm

	@TaskAction
	void installAdmin() {
		if (getProject().hasProperty("realm")) {
			realm = project.property("realm")
		}
		String username = project.property("mlUsername")
		String password = project.property("mlPassword")
		if (realm != null) {
			getAdminManager().installAdmin(username, password, realm)
		} else {
			getAdminManager().installAdmin(username, password)
		}
	}
}
