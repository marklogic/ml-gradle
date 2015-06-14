package com.marklogic.gradle.task.security

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.gradle.xcc.XccHelper

class SecurityTask extends MarkLogicTask {

    String xccUrl

    SecurityHelper getSecurityHelper() {
        if (!xccUrl) {
            xccUrl = getAppConfig().getContentXccUrl()
        }
        new SecurityHelper(new XccHelper(xccUrl))
    }
}
