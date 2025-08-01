/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.roxy

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Internal

class RoxyTask extends MarkLogicTask {

	@Internal
	String getRoxyProjectPath() {
		project.hasProperty("roxyProjectPath") ? project.property("roxyProjectPath") : null
	}

	void printMissingPathMessage() {
		println "The 'roxyProjectPath' property is not defined. Please run using -ProxyProjectPath=/path/to/your/roxy/project"
	}
}
