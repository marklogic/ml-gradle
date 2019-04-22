package com.marklogic.gradle.task.admin

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class InstallAdminTask extends MarkLogicTask {

	String realm

    @TaskAction
    void installAdmin() {
	    if (getProject().hasProperty("realm")) {
		    realm = project.property("realm")
	    }
	    if (realm != null) {
		    getAdminManager().installAdmin(getAdminUsername(), getAdminPassword(), realm)
	    } else {
		    getAdminManager().installAdmin(getAdminUsername(), getAdminPassword())
	    }
    }
}
