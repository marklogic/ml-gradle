package com.marklogic.gradle.task

import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DeleteModuleTimestampsFileTask extends DefaultTask {

    String filePath = PropertiesModuleManager.DEFAULT_FILE_PATH

    @TaskAction
    void deleteFile() {
        File f = new File(filePath)
        if (f.exists()) {
            println "Deleting " + f.getAbsolutePath() + "\n"
            f.delete()
        }
    }
}
