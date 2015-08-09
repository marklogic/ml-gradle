package com.rjrudin.marklogic.gradle.task.alerting

import com.rjrudin.marklogic.gradle.task.MarkLogicTask
import com.rjrudin.marklogic.gradle.xcc.XccHelper

class AlertTask extends MarkLogicTask {

    String xccUrl

    AlertHelper newAlertHelper() {
        if (!xccUrl) {
            xccUrl = getAppConfig().getContentXccUrl()
        }
        new AlertHelper(new XccHelper(xccUrl))
    }
}