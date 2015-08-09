package com.marklogic.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.modulesloader.impl.PropertiesModuleManager

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
