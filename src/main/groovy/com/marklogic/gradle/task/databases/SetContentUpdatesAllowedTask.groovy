package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class SetContentUpdatesAllowedTask extends MarkLogicTask {

    @TaskAction
    void clearModules() {
        if (project.hasProperty("mode")) {
            def mode = project.property("mode")
            println "Setting updates-allowed on each content forest to " + mode
            DatabaseManager mgr = new DatabaseManager(getManageClient())
            mgr.setUpdatesAllowedOnPrimaryForests(getAppConfig().getContentDatabaseName(), mode)
        } else {
            println "To set updates-allowed on each forest in the content database, include the mode parameter e.g. -Pmode=flash-backup"
            return
        }
    }
}
