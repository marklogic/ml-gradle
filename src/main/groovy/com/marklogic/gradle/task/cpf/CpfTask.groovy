package com.marklogic.gradle.task.cpf

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.gradle.xcc.XccHelper

class CpfTask extends MarkLogicTask {

    String xccUrl
    String triggersDatabaseName
    
    CpfHelper newCpfHelper() {
        if (!xccUrl) {
            xccUrl = getDefaultXccUrl()
        }
        def dbName = triggersDatabaseName ? triggersDatabaseName : getAppConfig().name + "-triggers"
        new CpfHelper(new XccHelper(xccUrl), dbName)
    }
}
