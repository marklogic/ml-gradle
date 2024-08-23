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
import com.marklogic.client.ext.helper.LoggingObject;

import java.util.List;

/**
 * Listeners should extend this so that new methods can be added to DeployerListener without breaking implementors.
 */
public class DeployerListenerSupport extends LoggingObject implements DeployerListener {

	@Override
	public void beforeCommandsExecuted(DeploymentContext context) {
	}

	@Override
	public void beforeCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands) {
	}

	@Override
	public void afterCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands) {
	}
}
