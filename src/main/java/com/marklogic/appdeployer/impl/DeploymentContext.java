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

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;

import java.util.List;

/**
 * Intended to capture all inputs of interest before commands are executed so that an e.g. DeployerListener can fiddle
 * around with them.
 */
public class DeploymentContext {

	private CommandContext commandContext;
	private AppConfig appConfig;
	private List<Command> commands;

	public DeploymentContext(CommandContext commandContext, AppConfig appConfig, List<Command> commands) {
		this.commandContext = commandContext;
		this.appConfig = appConfig;
		this.commands = commands;
	}

	public CommandContext getCommandContext() {
		return commandContext;
	}

	public void setCommandContext(CommandContext commandContext) {
		this.commandContext = commandContext;
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}
}
