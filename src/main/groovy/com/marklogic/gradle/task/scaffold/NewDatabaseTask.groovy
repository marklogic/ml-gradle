package com.marklogic.gradle.task.scaffold

import com.marklogic.mgmt.template.database.DatabaseTemplateBuilder
import org.gradle.api.tasks.TaskAction

class NewDatabaseTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		createResourceFile(new DatabaseTemplateBuilder())
	}

}

