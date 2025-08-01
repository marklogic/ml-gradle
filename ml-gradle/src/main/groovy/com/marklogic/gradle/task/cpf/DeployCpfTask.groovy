/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask

class DeployCpfTask extends MarkLogicTask {

    @TaskAction
    void deployCpf() {
        deployWithCommandListProperty("mlCpfCommands")
    }
}
