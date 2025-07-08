/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task

import com.marklogic.appdeployer.command.ResourceFileManagerImpl
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class DeleteResourceTimestampsFileTask extends MarkLogicTask {

	@Input
	@Optional
	String filePath

	@TaskAction
	void deleteResourceTimestampsFile() {
		if (filePath == null) {
			filePath = ResourceFileManagerImpl.DEFAULT_FILE_PATH
		}
		File f = new File(filePath)
		if (f.exists()) {
			println "Deleting " + f.getAbsolutePath() + "\n"
			f.delete()
		} else {
			println "Resource timestamps file " + filePath + " does not exist, so not deleting"
		}
	}
}
