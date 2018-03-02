package com.marklogic.gradle.task.scaffold

import com.marklogic.mgmt.template.security.UserTemplateBuilder
import org.gradle.api.tasks.TaskAction

class NewUserTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		createResourceFile(new UserTemplateBuilder())
	}
}
