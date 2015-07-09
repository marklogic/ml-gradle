package com.marklogic.gradle.task.viewschemas

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.viewschemas.CreateViewSchemasCommand
import com.marklogic.gradle.task.MarkLogicTask

class CreateViewSchemasTask extends MarkLogicTask {

    @TaskAction
    void updateViewSchemas() {
        new CreateViewSchemasCommand().execute(getCommandContext())
    }
}
