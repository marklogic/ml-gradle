package com.marklogic.gradle.task.scaffold

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.scaffold.ScaffoldGenerator
import com.marklogic.gradle.task.MarkLogicTask

class GenerateScaffoldTask extends MarkLogicTask {

    @TaskAction
    void generateScaffold() {
        ScaffoldGenerator g = new ScaffoldGenerator()
        def propName = "scaffoldPath"
        def path = project.hasProperty(propName) ? project.property(propName) : "."
        println "Generating scaffold for path: " + path
        g.generateScaffold(path, getAppConfig())
    }
}
