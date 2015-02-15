package com.marklogic.gradle.task

import org.gradle.api.DefaultTask;

import com.marklogic.gradle.AppConfig;

class MarkLogicTask extends DefaultTask {

    AppConfig getAppConfig() {
        getProject().property("mlAppConfig")
    }
    
    String getDefaultXccUrl() {
        getAppConfig().getXccUrl()
    }
    
    boolean isTestPortSet() {
        getAppConfig().getTestRestPort() != null && getAppConfig().getTestRestPort() > 0
    }
}
