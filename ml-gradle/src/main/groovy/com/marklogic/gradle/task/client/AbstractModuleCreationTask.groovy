/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.client

import com.marklogic.gradle.task.MarkLogicTask

class AbstractModuleCreationTask extends MarkLogicTask{

	/**
	 * Select the first modules path that is a valid location for creating a new module. Intent is to filter out
	 * paths containing "mlBundle" as those almost certainly point to directories where bundles have been
	 * unzipped to, and we don't want to create new modules in those directories.
	 *
	 * @return
	 */
	String selectModulesPath() {
		String path
		List<String> modulePaths = getAppConfig().getModulePaths()
		for (String modulePath : modulePaths) {
			if (modulePath != null && !modulePath.contains("mlBundle")) {
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
