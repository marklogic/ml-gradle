/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class DeleteModuleTimestampsFileTask extends MarkLogicTask {

	@Input
	@Optional
	String filePath;

    @TaskAction
    void deleteFile() {
	    filePath = getAppConfig().getModuleTimestampsPath()
		if (filePath != null && filePath.trim().length() > 0) {
			File f = new File(filePath)
			if (f.exists()) {
				println "Deleting " + f.getAbsolutePath() + "\n"
				f.delete()
			} else {
				println "Module timestamps file " + filePath + " does not exist, so not deleting"
			}
		} else {
			println "Module timestamps file path is not set, so not attempting to delete"
		}
    }
}
