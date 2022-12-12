package com.marklogic.gradle.task.security

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class UndeploySecureCredentialsTask extends MarkLogicTask {

	@TaskAction
	void undeploySecureCredentialsSecurity() {
		undeployWithCommandWithClassName("DeploySecureCredentialsCommand")
	}
}
