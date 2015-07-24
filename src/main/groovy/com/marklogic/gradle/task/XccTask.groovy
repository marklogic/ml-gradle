package com.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.xcc.XccHelper

/**
 * Simple task for easing the execution of ad hoc XQuery.
 */
class XccTask extends MarkLogicTask {

    String xquery
    String xccUrl

    @TaskAction
    void executeXcc() {
        new XccHelper(xccUrl).executeXquery(xquery)
    }
}
