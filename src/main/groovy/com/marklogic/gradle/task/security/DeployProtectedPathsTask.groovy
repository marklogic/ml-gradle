package com.marklogic.gradle.task.security

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployProtectedPathsTask extends MarkLogicTask {

	@TaskAction
	void deployProtectedPaths() {
		invokeDeployerCommandWithClassName("DeployProtectedPathsCommand")
	}
}
