package com.marklogic.gradle.task.security

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class UndeployQueryRolesetsTask extends MarkLogicTask {

	@TaskAction
	void undeployQueryRolesets() {
		undeployWithCommandWithClassName("DeployQueryRolesetsCommand")
	}

}
