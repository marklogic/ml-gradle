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
		if (realm != null) {
			getAdminManager().installAdmin(getAdminUsername(), getAdminPassword(), realm)
		} else {
			getAdminManager().installAdmin(getAdminUsername(), getAdminPassword())
		}
	}
}
