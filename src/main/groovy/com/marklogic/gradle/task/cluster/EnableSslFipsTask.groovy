package com.marklogic.gradle.task.cluster

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.admin.SetSslFipsEnabledCommand
import com.marklogic.gradle.task.MarkLogicTask

class EnableSslFipsTask extends MarkLogicTask {

    @TaskAction
    void disableSslFips() {
        new SetSslFipsEnabledCommand(true).execute(getCommandContext())
    }
}