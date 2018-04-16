package com.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction

class DeleteModuleTimestampsFileTask extends MarkLogicTask {

    @TaskAction
    void deleteFile() {
	    String filePath = getAppConfig().getModuleTimestampsPath()
        File f = new File(filePath)
        if (f.exists()) {
            println "Deleting " + f.getAbsolutePath() + "\n"
            f.delete()
        } else {
	        println "Module timestamps file " + filePath + " does not exist, so not deleting"
        }
    }
}
