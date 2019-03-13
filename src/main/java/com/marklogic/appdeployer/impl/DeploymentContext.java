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
