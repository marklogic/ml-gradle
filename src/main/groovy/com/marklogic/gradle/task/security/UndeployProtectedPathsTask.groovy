package com.marklogic.gradle.task.security

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class UndeployProtectedPathsTask extends MarkLogicTask {

	@TaskAction
	void undeployProtectedPaths() {
		undeployWithCommandWithClassName("DeployProtectedPathsCommand")
	}

}
