package com.marklogic.gradle.task.scaffold

import com.marklogic.mgmt.template.security.ExternalSecurityTemplateBuilder
import org.gradle.api.tasks.TaskAction

class NewExternalSecurityTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		createResourceFile(new ExternalSecurityTemplateBuilder())
	}

}
