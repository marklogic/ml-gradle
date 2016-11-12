package com.marklogic.gradle.task.admin

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class InstallAdminTask extends MarkLogicTask {

    String adminUsername
    String adminPassword

    @TaskAction
    void installAdmin() {
		if (project.hasProperty("adminUsername")) {
			adminUsername = project.property("adminUsername")
		}
		if (project.hasProperty("adminPassword")) {
			adminPassword = project.property("adminPassword")
		}
        getAdminManager().installAdmin(adminUsername, adminPassword)
    }
}
