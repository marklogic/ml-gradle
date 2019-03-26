package com.marklogic.gradle.task.data

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class LoadDataTask extends MarkLogicTask {

	@TaskAction
	void loadData() {
		deployWithCommandListProperty("mlDataCommands")
	}
}
