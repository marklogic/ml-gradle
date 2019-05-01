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
