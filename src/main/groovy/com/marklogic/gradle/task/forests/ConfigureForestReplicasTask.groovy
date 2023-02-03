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
package com.marklogic.gradle.task.forests

import com.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Task for executing an instance of ConfigureForestReplicasCommand. The command is exposed as a task attribute so
 * that its map of forest names and replica counts and be configured easily in a Gradle build file.
 */
class ConfigureForestReplicasTask extends MarkLogicTask {

	@Input
	@Optional
	ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand()

	@TaskAction
	void configureForestReplicas() {
		command.execute(getCommandContext())
	}
}
