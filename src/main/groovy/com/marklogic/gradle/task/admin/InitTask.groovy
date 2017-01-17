package com.marklogic.gradle.task.admin

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class InitTask extends MarkLogicTask {

    String licenseKey
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
