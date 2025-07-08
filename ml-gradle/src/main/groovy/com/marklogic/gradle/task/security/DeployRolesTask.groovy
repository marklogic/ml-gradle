/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployRolesTask extends MarkLogicTask {

    @TaskAction
    void deployRoles() {
        invokeDeployerCommandWithClassName("DeployRolesCommand")
    }
}
