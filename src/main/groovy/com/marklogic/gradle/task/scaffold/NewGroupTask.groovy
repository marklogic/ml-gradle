package com.marklogic.gradle.task.scaffold

import com.marklogic.mgmt.template.group.GroupTemplateBuilder
import org.gradle.api.tasks.TaskAction

class NewGroupTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		createResourceFile(new GroupTemplateBuilder())
	}

}

