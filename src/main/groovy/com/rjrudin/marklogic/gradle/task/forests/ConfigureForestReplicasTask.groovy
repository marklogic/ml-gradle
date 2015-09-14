package com.rjrudin.marklogic.gradle.task.forests

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

/**
 * Task for executing an instance of ConfigureForestReplicasCommand. The command is exposed as a task attribute so
 * that its map of forest names and replica counts and be configured easily in a Gradle build file.
 */
class ConfigureForestReplicasTask extends MarkLogicTask {

    ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand()

    @TaskAction
    void configureReplicaForests() {
        command.execute(getCommandContext())
    }
}
