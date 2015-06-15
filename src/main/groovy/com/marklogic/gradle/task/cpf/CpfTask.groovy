package com.marklogic.gradle.task.cpf

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.gradle.xcc.XccHelper

/**
 * Base class for CPF tasks that depend on XCC. These will be replaceable in ML8 with REST API calls.
 */
class CpfTask extends MarkLogicTask {

    String xccUrl
    String triggersDatabaseName
    
    CpfHelper newCpfHelper() {
        if (!xccUrl) {
            xccUrl = getAppConfig().getContentXccUrl()
        }
        def dbName = triggersDatabaseName ? triggersDatabaseName : getAppConfig().name + "-triggers"
        new CpfHelper(new XccHelper(xccUrl), dbName)
    }
}
