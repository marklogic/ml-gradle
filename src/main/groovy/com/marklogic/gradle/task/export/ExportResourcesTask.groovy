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
package com.marklogic.gradle.task.export

import com.marklogic.appdeployer.ConfigDir
import com.marklogic.appdeployer.export.ExportedResources
import com.marklogic.appdeployer.export.Exporter
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.selector.PrefixResourceSelector
import com.marklogic.mgmt.selector.PropertiesResourceSelector
import com.marklogic.mgmt.selector.RegexResourceSelector
import com.marklogic.mgmt.selector.ResourceSelector
import org.gradle.api.tasks.TaskAction

class ExportResourcesTask extends MarkLogicTask {

	@TaskAction
	void exportResources() {
		String filePropName = "propertiesFile"
		String prefixPropName = "prefix"
		String regexPropName = "regex"
		String triggersDatabase = getAppConfig().getTriggersDatabaseName()

		String includeTypesPropName = "includeTypes"
		String includeTypes = null
		if (getProject().hasProperty(includeTypesPropName)) {
			includeTypes = getProject().property(includeTypesPropName)
		}

		if (getProject().hasProperty(filePropName)) {
			String filename = getProject().property(filePropName)
			File file = new File(filename)
			if (file.exists()) {
				export(new PropertiesResourceSelector(file))
			} else {
				println "File " + filename + " does not exist"
			}
		} else if (getProject().hasProperty(prefixPropName)) {
			String prefix = getProject().property(prefixPropName)
			PrefixResourceSelector selector = new PrefixResourceSelector(prefix)
			selector.setTriggersDatabase(triggersDatabase)
			selector.setIncludeTypesAsString(includeTypes)
			export(selector)
		} else if (getProject().hasProperty(regexPropName)) {
			String regex = getProject().property(regexPropName)
			RegexResourceSelector selector = new RegexResourceSelector(regex)
			selector.setTriggersDatabase(triggersDatabase)
			selector.setIncludeTypesAsString(includeTypes)
			export(selector)
		} else {
			println "Use -PpropertiesFile or -Pprefix or -Pregex to specify the resources to export, with -PincludeTypes=comma-delimited-string for restricting the types to export when using -Pprefix or -Pregex, and -PexportPath to customize where to export the resources to"
		}
	}

	void export(ResourceSelector selector) {
		List<ConfigDir> configDirs = getAppConfig().getConfigDirs()
		ConfigDir lastConfigDir = configDirs.get(configDirs.size() - 1)
		def path = lastConfigDir.getBaseDir()
		if (getProject().hasProperty("exportPath")) {
			/**
			 * Note that if a user provides a path and they're using Java 11 and the Gradle daemon, then the path
			 * should be absolute. Otherwise, it will be resolved from the directory that the daemon was launched
			 * from, which is likely not desirable.
			 */
			path = new File(getProject().property("exportPath"))
		}
		println "Exporting resources to: " + path

		Exporter exporter
		if (project.hasProperty("mlGroupName")) {
			String group = project.property("mlGroupName")
			println "Will export servers and tasks in group: " + group
			exporter = new Exporter(getManageClient(), group)
		} else {
			exporter = new Exporter(getManageClient())
		}

		ExportedResources resources = exporter
			.withTriggersDatabase(getAppConfig().getTriggersDatabaseName())
			.select(selector)
			.export(path)

		println "Exported files:"
		if (resources.getFiles() != null) {
			for (File f : resources.getFiles()) {
				println f.getAbsolutePath()
			}
		}

		println "Export messages:"
		if (resources.getMessages() != null) {
			for (String s : resources.getMessages()) {
				println s
			}
		}
	}
}
