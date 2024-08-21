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
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple implementation that allows for a list of commands to be set.
 */
public class SimpleAppDeployer extends AbstractAppDeployer {

	private List<Command> commands;

	/**
	 * @param commandArray
	 * @deprecated since 4.5.0; avoid using since it assumes the use of default passwords
	 */
	@Deprecated
	public SimpleAppDeployer(Command... commandArray) {
		super();
		buildModifiableCommandList(commandArray);
	}

	/**
	 * @param commands
	 * @deprecated since 4.5.0; avoid using since it assumes the use of default passwords
	 */
	@Deprecated
	public SimpleAppDeployer(List<Command> commands) {
		this.commands = commands;
	}

	public SimpleAppDeployer(ManageClient manageClient, AdminManager adminManager, Command... commandArray) {
		super(manageClient, adminManager);
		buildModifiableCommandList(commandArray);
	}

	/**
	 * Arrays.asList produces an unmodifiable list, but we want a client to be able to modify the list.
	 *
	 * @param commandArray
	 */
	protected void buildModifiableCommandList(Command... commandArray) {
		if (commandArray != null) {
			commands = new ArrayList<>(commandArray.length);
			commands.addAll(Arrays.asList(commandArray));
		} else {
			commands = new ArrayList<>();
		}
	}

	/**
	 * Convenience method for finding a command of a certain type, presumably so it can be modified/reconfigured.
	 *
	 * @param clazz
	 * @return
	 */
	public Command getCommandOfType(Class<?> clazz) {
		for (Command c : commands) {
			if (c.getClass().equals(clazz)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Convenience method for finding a command with the given short class name (i.e. no package info in it), presumably so it can be
	 * modified/reconfigured.
	 *
	 * @param shortClassName
	 * @return
	 */
	public Command getCommand(String shortClassName) {
		for (Command c : commands) {
			if (c.getClass().getSimpleName().equals(shortClassName)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Remove the first command that has the same short class name as the given argument, and return it
	 * if it's removed.
	 *
	 * @param shortClassName
	 * @return
	 */
	public Command removeCommand(String shortClassName) {
		for (Command c : commands) {
			if (c.getClass().getSimpleName().equals(shortClassName)) {
				commands.remove(c);
				return c;
			}
		}
		return null;
	}

	/**
	 * Keep this public so that a client can easily manipulate the list in case a default set of commands has been
	 * provided.
	 */
	@Override
	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

}
