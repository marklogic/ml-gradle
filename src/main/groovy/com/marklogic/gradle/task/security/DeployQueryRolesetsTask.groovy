package com.marklogic.gradle.task.security

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployQueryRolesetsTask extends MarkLogicTask {

	@TaskAction
	void deployQueryRolesets() {
		invokeDeployerCommandWithClassName("DeployQueryRolesetsCommand")
	}

}
