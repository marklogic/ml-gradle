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
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract base class that just needs the subclass to define the list of Command instances to use. Handles executing
 * commands in sorted order.
 */
public abstract class AbstractAppDeployer extends LoggingObject implements AppDeployer {

	private ManageClient manageClient;
	private AdminManager adminManager;
	private List<DeployerListener> deployerListeners;

	/**
	 * @deprecated since 4.5.0; avoid using since it assumes the use of default passwords
	 */
	@Deprecated
	public AbstractAppDeployer() {
		this(new ManageClient(), new AdminManager());
	}

	public AbstractAppDeployer(ManageClient manageClient, AdminManager adminManager) {
		super();
		this.manageClient = manageClient;
		this.adminManager = adminManager;

		this.deployerListeners = new ArrayList<>();
		this.deployerListeners.add(new AddHostNameTokensDeployerListener());
		this.deployerListeners.add(new PrepareCommandListener());
		this.deployerListeners.add(new CmaDeployerListener());
	}

	/**
	 * The subclass just needs to define the list of commands to be invoked.
	 *
	 * @return
	 */
	protected abstract List<Command> getCommands();

	/**
	 * Calls execute on each of the configured commands.
	 *
	 * @param appConfig
	 */
	public void deploy(AppConfig appConfig) {
		List<String> configPaths = new ArrayList<>();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			configPaths.add(configDir.getBaseDir().getAbsolutePath());
		}
		logger.info(format("Deploying app %s with config dirs: %s\n", appConfig.getName(), configPaths));

		List<Command> commands = getCommands();
		Collections.sort(commands, new ExecuteComparator());

		CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

		final DeploymentContext deploymentContext = new DeploymentContext(context, appConfig, commands);

		deployerListeners.forEach(listener -> listener.beforeCommandsExecuted(deploymentContext));

		boolean catchExceptions = appConfig.isCatchDeployExceptions();

		int commandCount = commands.size();
		for (int i = 0; i < commandCount; i++) {
			Command command = commands.get(i);
			final List<Command> remainingCommands = commands.subList(i + 1, commandCount);
			String name = command.getClass().getName();

			logger.info(format("Executing command [%s] with sort order [%d]", name, command.getExecuteSortOrder()));
			invokeListenersBeforeCommandExecuted(context, command, deploymentContext, remainingCommands, catchExceptions);
			long start = System.currentTimeMillis();
			executeCommand(command, context);
			logger.info(format("Finished executing command [%s] in %dms\n", name, (System.currentTimeMillis() - start)));
			invokeListenersAfterCommandExecuted(context, command, deploymentContext, remainingCommands, catchExceptions);
		}

		logger.info(format("Deployed app %s", appConfig.getName()));
	}

	/**
	 * Executes the command, catching an exception if desired.
	 *
	 * @param command
	 * @param context
	 */
	protected void executeCommand(Command command, CommandContext context) {
		try {
			command.execute(context);
		} catch (RuntimeException ex) {
			if (context.getAppConfig().isCatchDeployExceptions()) {
				logger.error(format("Command [%s] threw exception that was caught; cause: %s", command.getClass().getName(), ex.getMessage()), ex);
			} else {
				throw ex;
			}
		}
	}

	/**
	 * Calls undo on each of the configured commands that implements the UndoableCommand interface.
	 *
	 * @param appConfig
	 */
	public void undeploy(AppConfig appConfig) {
		List<String> configPaths = new ArrayList<>();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			configPaths.add(configDir.getBaseDir().getAbsolutePath());
		}
		logger.info(format("Undeploying app %s with config dirs: %s\n", appConfig.getName(), configPaths));

		List<Command> commands = getCommands();

		List<UndoableCommand> undoableCommands = new ArrayList<>();
		for (Command command : commands) {
			if (command instanceof UndoableCommand) {
				undoableCommands.add((UndoableCommand) command);
			}
		}

		Collections.sort(undoableCommands, new UndoComparator());
		CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

		final DeploymentContext deploymentContext = new DeploymentContext(context, appConfig, commands);
		deployerListeners.forEach(listener -> listener.beforeCommandsExecuted(deploymentContext));

		boolean catchExceptions = appConfig.isCatchUndeployExceptions();

		int commandCount = undoableCommands.size();
		for (int i = 0; i < commandCount; i++) {
			UndoableCommand command = undoableCommands.get(i);
			final List<Command> remainingCommands = commands.subList(i + 1, commandCount);

			String name = command.getClass().getName();
			logger.info(format("Undoing command [%s] with sort order [%d]", name, command.getUndoSortOrder()));
			invokeListenersBeforeCommandExecuted(context, command, deploymentContext, remainingCommands, catchExceptions);
			undoCommand(command, context);
			logger.info(format("Finished undoing command [%s]\n", name));
			invokeListenersAfterCommandExecuted(context, command, deploymentContext, remainingCommands, catchExceptions);
		}

		logger.info(format("Undeployed app %s", appConfig.getName()));
	}

	/**
	 * Calls undo on the command, catching an exception if desired.
	 *
	 * @param command
	 * @param context
	 */
	protected void undoCommand(UndoableCommand command, CommandContext context) {
		try {
			command.undo(context);
		} catch (RuntimeException ex) {
			if (context.getAppConfig().isCatchUndeployExceptions()) {
				logger.error(format("Command [%s] threw exception that was caught; cause: %s", command.getClass().getName(), ex.getMessage()), ex);
			} else {
				throw ex;
			}
		}
	}

	protected void invokeListenersBeforeCommandExecuted(CommandContext context, Command command, DeploymentContext deploymentContext,
	                                                    List<Command> remainingCommands, boolean catchExceptions) {
		deployerListeners.forEach(listener -> {
			try {
				listener.beforeCommandExecuted(command, deploymentContext, remainingCommands);
			} catch (Exception ex) {
				if (catchExceptions) {
					logger.error(format("Listener threw exception that was caught; cause: %s", ex.getMessage()), ex);
				} else {
					throw ex;
				}
			}
		});
	}

	protected void invokeListenersAfterCommandExecuted(CommandContext context, Command command, DeploymentContext deploymentContext,
	                                                   List<Command> remainingCommands, boolean catchExceptions) {
		deployerListeners.forEach(listener -> {
			try {
				listener.afterCommandExecuted(command, deploymentContext, remainingCommands);
			} catch (Exception ex) {
				if (catchExceptions) {
					logger.error(format("Listener threw exception that was caught; cause: %s", ex.getMessage()), ex);
				} else {
					throw ex;
				}
			}
		});
	}

	public List<DeployerListener> getDeployerListeners() {
		return deployerListeners;
	}

	public void setDeployerListeners(List<DeployerListener> deployerListeners) {
		this.deployerListeners = deployerListeners;
	}
}

class ExecuteComparator implements Comparator<Command> {
	@Override
	public int compare(Command o1, Command o2) {
		return o1.getExecuteSortOrder().compareTo(o2.getExecuteSortOrder());
	}
}

class UndoComparator implements Comparator<UndoableCommand> {
	@Override
	public int compare(UndoableCommand o1, UndoableCommand o2) {
		return o1.getUndoSortOrder().compareTo(o2.getUndoSortOrder());
	}
}
