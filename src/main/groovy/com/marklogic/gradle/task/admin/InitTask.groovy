package com.marklogic.gradle.task.admin

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class InitTask extends MarkLogicTask {

    String licenseKey
    String licensee

    @TaskAction
    void initializeMarkLogic() {
		if (project.hasProperty("licenseKey")) {
			licenseKey = project.property("licenseKey")
		}
		if (project.hasProperty("licensee")) {
			licensee = project.property("licensee")
		}
        getAdminManager().init(licenseKey, licensee)
    }
}
