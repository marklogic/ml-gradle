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

import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.CommandMapBuilder
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import java.util.function.Supplier

class DeployToReplicaTask extends MarkLogicTask {

	@Input
	@Optional
	// Intended to allow for DHF, or even a user, to provide a custom list of commands.
	// A user may wish to do this so that e.g. security commands are not included, in case the Security database is
	// being replicated.
	Supplier<List<Command>> commandListSupplier

	@TaskAction
	void deployToReplica() {
		List<Command> commands = commandListSupplier != null ?
			commandListSupplier.get() :
			new CommandMapBuilder().getCommandsForReplicaCluster()

		new SimpleAppDeployer(commands).deploy(getAppConfig())
	}
}
