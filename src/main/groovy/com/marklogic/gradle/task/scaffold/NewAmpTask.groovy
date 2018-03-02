package com.marklogic.gradle.task.scaffold

import com.marklogic.mgmt.template.security.AmpTemplateBuilder
import org.gradle.api.tasks.TaskAction

class NewAmpTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		createResourceFile(new AmpTemplateBuilder())
	}

}
