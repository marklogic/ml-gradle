/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.scaffold


import com.marklogic.mgmt.template.trigger.TriggerTemplateBuilder
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class NewTriggerTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		if (project.hasProperty("database")) {
			createResourceFile(new TriggerTemplateBuilder(project.property("database")))
		} else {
			throw new GradleException("Please specify a database for the trigger via -Pdatabase=(name of triggers database)")
		}
	}

}
