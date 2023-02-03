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
package com.marklogic.gradle.task.shell

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

/**
 * Convenience task for using ml-groovysh and setting up an API instance. This task copies all Gradle properties
 * starting with "ml" into the groovysh environment as system properties.
 *
 * By setting -PmlShellWatchModules=true, a thread will be launched within groovysh that behaves the same as the
 * mlWatch task - i.e. loading new/modified modules.
 *
 * You can add to the groovysh initialization script by setting the Gradle property mlShellScript.
 *
 * If this task does not meet your needs for integrating Gradle and groovy, you can always write your own task with
 * your own startup script, perhaps using this as a starting point.
 */
class ShellTask extends JavaExec {

	// This no longer specifies a mainClass, as the way that's done changed between Gradle 6.3 and 6.6.
	// The docs for this task will be updated so that a user knows how to set the mainClass in the rare event
	// that this task is actually used (this was included as a prototype and likely should never have been registered
	// in MarkLogicPlugin).
	
	@TaskAction
	@Override
	void exec() {
		setStandardInput(System.in)

		Project project = getProject()

		def mlShellJvmArgs = []
		for (String key : project.getProperties().keySet()) {
			if (key.startsWith("ml")) {
				mlShellJvmArgs.push("-D" + key + "=" + project.property(key))
			}
		}
		setJvmArgs(mlShellJvmArgs)

		// Create an instance of the API class
		def script = "ml = com.marklogic.mgmt.api.APIUtil.newAPIFromSystemProps()"

		// Enable loading modules if desired
		if (project.hasProperty("mlShellWatchModules") && project.property("mlShellWatchModules").equals("true")) {
			script += "\ncom.marklogic.appdeployer.util.ModulesWatcher.startFromSystemProps()"
		}

		// Allow project to add to the shell initialization script; mlShellScript will need to be in gradle.properties
		if (project.hasProperty("mlShellScript")) {
			script += "\n" + project.property("mlShellScript")
		}

		def mlShellArgs = ["-e", script]
		setArgs(mlShellArgs)

		super.exec()
	}
}
