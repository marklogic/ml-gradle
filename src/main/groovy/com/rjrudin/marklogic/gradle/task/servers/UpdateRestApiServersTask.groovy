package com.rjrudin.marklogic.gradle.task.servers

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class UpdateRestApiServersTask extends MarkLogicTask {

    @TaskAction
    void updateRestApiServers() {
        new UpdateRestApiServersCommand().execute(getCommandContext())
    }
}
