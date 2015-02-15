package com.marklogic.gradle.task.client.service

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.RestHelper
import com.marklogic.gradle.task.client.ClientTask;

class InstallAssetTask extends ClientTask {

    String assetPath
    String urlPath
    String requestContentType
    String format = "text"

    @TaskAction
    void installAsset() {
        try {
            newRestHelper().installAsset(assetPath, requestContentType, urlPath, format)
        } catch (Exception e) {
            println "Unable to install asset from ${assetPath}; cause: " + e.getMessage()
        }
    }
}
