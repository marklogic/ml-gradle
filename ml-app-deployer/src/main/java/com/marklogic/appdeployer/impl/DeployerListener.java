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
package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.command.Command;

import java.util.List;

/**
 * Provides an extension point for introducing behavior into AbstractAppDeployer.
 */
public interface DeployerListener {

	void beforeCommandsExecuted(DeploymentContext context);

	void beforeCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands);

	/**
	 * Invoked after a command is executed (does not include "undo" being invoked on a command).
	 *
	 * @param command
	 * @param context
	 * @param remainingCommands
	 */
	void afterCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands);

}
