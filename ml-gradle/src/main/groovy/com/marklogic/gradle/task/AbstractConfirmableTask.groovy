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

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Parent class for tasks that benefit from some confirmation in the way of requiring a property named "confirm" to be
 * set to "true". Tasks that can benefit from this simply need to extend this and implement the
 * "executeIfConfirmed" method.
 */
abstract class AbstractConfirmableTask extends MarkLogicTask {

	@TaskAction
	void executeTask() {
		boolean executed = false
		if (project.hasProperty("confirm")) {
			if ("true".equals(project.property("confirm"))) {
				executed = true
				executeIfConfirmed()
			}
		}

		// Throwing an exception so that any tasks that are run after this task are not executed either
		if (!executed) {
			throw new GradleException("To execute this task, set the 'confirm' property to 'true'; e.g. '-Pconfirm=true'")
		}
	}

	abstract void executeIfConfirmed()
}
