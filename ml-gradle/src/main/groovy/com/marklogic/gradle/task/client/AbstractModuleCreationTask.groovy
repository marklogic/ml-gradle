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
package com.marklogic.gradle.task.client

import com.marklogic.gradle.task.MarkLogicTask

class AbstractModuleCreationTask extends MarkLogicTask{

	/**
	 * Select the first modules path that is a valid location for creating a new module. Intent is to filter out
	 * paths containing "mlRestApi" or "mlBundle" as those almost certainly point to directories where bundles have been
	 * unzipped to, and we don't want to create new modules in those directories.
	 *
	 * @return
	 */
	String selectModulesPath() {
		String path
		List<String> modulePaths = getAppConfig().getModulePaths()
		for (String modulePath : modulePaths) {
			if (modulePath != null && !modulePath.contains("mlRestApi") && !modulePath.contains("mlBundle")) {
				path = modulePath
				break
			}
		}

		// Should never get here, but need to pick a path if we do
		if (path == null) {
			path = modulePaths.get(modulePaths.size() - 1)
		}

		return path
	}
}
