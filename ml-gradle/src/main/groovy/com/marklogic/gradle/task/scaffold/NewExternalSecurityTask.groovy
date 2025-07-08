/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.scaffold

import com.marklogic.mgmt.template.security.ExternalSecurityTemplateBuilder
import org.gradle.api.tasks.TaskAction

class NewExternalSecurityTask extends NewResourceTask {

	@TaskAction
	void newResource() {
		createResourceFile(new ExternalSecurityTemplateBuilder())
	}

}
