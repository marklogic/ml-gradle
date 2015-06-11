package com.marklogic.gradle.task.servers

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.servers.UpdateRestApiServersCommand
import com.marklogic.gradle.task.MarkLogicTask

class UpdateRestApiServersTask extends MarkLogicTask {

    @TaskAction
    void updateRestApiServers() {
        new UpdateRestApiServersCommand().execute(getCommandContext())
    }
}
