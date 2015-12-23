package com.marklogic.gradle.task.cluster

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.admin.SetSslFipsEnabledCommand
import com.marklogic.gradle.task.MarkLogicTask

class DisableSslFipsTask extends MarkLogicTask {

    @TaskAction
    void disableSslFips() {
        new SetSslFipsEnabledCommand(false).execute(getCommandContext())
    }
}
