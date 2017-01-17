package com.marklogic.gradle.task.admin

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class InstallAdminTask extends MarkLogicTask {

    @TaskAction
    void installAdmin() {
        getAdminManager().installAdmin(getAdminUsername(), getAdminPassword())
    }
}
