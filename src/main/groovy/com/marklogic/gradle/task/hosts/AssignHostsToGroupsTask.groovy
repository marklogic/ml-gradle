package com.marklogic.gradle.task.hosts

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask

class AssignHostsToGroupsTask extends MarkLogicTask {

    @TaskAction
    void assignHostsToGroups() {
        invokeDeployerCommandWithClassName("AssignHostsToGroupsCommand")
    }
}
