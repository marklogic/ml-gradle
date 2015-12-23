package com.marklogic.gradle.task.forests

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand
import com.marklogic.gradle.task.MarkLogicTask

/**
 * Task for invoking "undo" on an instance of ConfigureForestReplicasCommand. The command is exposed as a task 
 * attribute so that its map of forest names and replica counts and be configured easily in a Gradle build file. 
 */
class DeleteForestReplicasTask extends MarkLogicTask {

    ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand()

    @TaskAction
    void deleteForestReplicas() {
        command.undo(getCommandContext())
    }
}
