package com.marklogic.gradle.task.alerting

import org.gradle.api.tasks.TaskAction

class InsertAlertActionTask extends AlertTask {

    String configUri
    String actionName
    String actionDescription
    String module
    String options = "<options xmlns='http://marklogic.com/xdmp/alert'/>"

    @TaskAction
    void insertAlertAction() {
        newAlertHelper().insertAlertAction(configUri, actionName, actionDescription, module, options)
        println "Inserted alert action with name: " + actionName + "\n"
    }
}
