package com.marklogic.gradle.task.alerting

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.gradle.xcc.XccHelper

class AlertTask extends MarkLogicTask {

    String xccUrl

    AlertHelper newAlertHelper() {
        if (!xccUrl) {
            xccUrl = getDefaultXccUrl()
        }
        new AlertHelper(new XccHelper(xccUrl))
    }
}