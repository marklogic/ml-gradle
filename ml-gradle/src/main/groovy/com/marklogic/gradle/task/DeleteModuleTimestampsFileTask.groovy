/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
