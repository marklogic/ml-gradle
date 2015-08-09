package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand
import com.marklogic.gradle.task.MarkLogicTask

class UpdateContentDatabasesTask extends MarkLogicTask {

    @TaskAction
    void updateContentDatabases() {
        new UpdateContentDatabasesCommand().execute(getCommandContext())
    }
}
