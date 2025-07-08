/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.scaffold

import com.marklogic.appdeployer.scaffold.ScaffoldGenerator
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class GenerateScaffoldTask extends MarkLogicTask {

    @TaskAction
    void generateScaffold() {
        ScaffoldGenerator g = new ScaffoldGenerator()
        def propName = "scaffoldPath"
        def path = project.hasProperty(propName) ? project.property(propName) : getProject().getProjectDir().getAbsolutePath()
        println "Generating scaffold for path: " + path
		ScaffoldGenerator.AppInputs appInputs = new ScaffoldGenerator.AppInputs(getAppConfig().getName())
        g.generateScaffold(path, appInputs)
    }
}
