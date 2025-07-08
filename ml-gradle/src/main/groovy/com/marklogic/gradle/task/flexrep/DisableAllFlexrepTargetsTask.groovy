/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.flexrep

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.flexrep.ConfigManager

class DisableAllFlexrepTargetsTask extends MarkLogicTask {

    @TaskAction
    void disableAllFlexrepTargets() {
        new ConfigManager(getManageClient(), getAppConfig().getContentDatabaseName()).disableAllFlexrepTargets()
    }
}
