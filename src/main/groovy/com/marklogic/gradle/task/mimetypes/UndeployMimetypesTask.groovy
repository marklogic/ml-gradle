package com.marklogic.gradle.task.mimetypes

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class UndeployMimetypesTask extends MarkLogicTask {

	@TaskAction
	void undeployMimetypes() {
		undeployWithCommandListProperty("mlMimetypeCommands")
	}
}
