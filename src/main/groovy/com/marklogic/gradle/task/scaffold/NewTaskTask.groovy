package com.marklogic.gradle.task.scaffold

import com.marklogic.mgmt.template.task.TaskTemplateBuilder
import org.gradle.api.tasks.TaskAction

class NewTaskTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		createResourceFile(new TaskTemplateBuilder())
	}

}
