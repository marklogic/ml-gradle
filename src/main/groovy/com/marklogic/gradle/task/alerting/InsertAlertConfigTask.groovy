package com.marklogic.gradle.task.alerting

import org.gradle.api.tasks.TaskAction

class InsertAlertConfigTask extends AlertTask {

    String configUri
    String configName
    String configDescription
    String options = "<options xmlns='http://marklogic.com/xdmp/alert'/>"
    String[] cpfDomainNames

    @TaskAction
    void insertAlertConfig() {
        newAlertHelper().insertAlertConfig(configUri, configName, configDescription, options, cpfDomainNames)
        println "Inserted alert config with URI: " + configUri
    }
}
