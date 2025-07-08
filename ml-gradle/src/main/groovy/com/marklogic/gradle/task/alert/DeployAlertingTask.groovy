/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.alert

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployAlertingTask extends MarkLogicTask {

    @TaskAction
    void deployAlerting() {
        deployWithCommandListProperty("mlAlertCommands")
    }
}
