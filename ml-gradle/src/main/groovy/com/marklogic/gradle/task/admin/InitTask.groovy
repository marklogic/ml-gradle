/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.admin

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class InitTask extends MarkLogicTask {

	@Input
	@Optional
    String licenseKey

	@Input
	@Optional
    String licensee

    @TaskAction
    void initializeMarkLogic() {
		if (project.hasProperty("mlLicenseKey")) {
			licenseKey = project.property("mlLicenseKey")
		}
		if (project.hasProperty("mlLicensee")) {
			licensee = project.property("mlLicensee")
		}
        getAdminManager().init(licenseKey, licensee)
    }
}
