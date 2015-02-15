package com.marklogic.gradle.task.alerting

import org.gradle.api.tasks.TaskAction

class DeleteAlertConfigTask extends AlertTask {

    String configUri
    boolean deleteRules = false
    
    @TaskAction
    void deleteAlertConfig() {
        newAlertHelper().deleteAlertConfig(configUri, deleteRules)
        println "Deleted alert config: " + configUri + "\n"
    }
}
