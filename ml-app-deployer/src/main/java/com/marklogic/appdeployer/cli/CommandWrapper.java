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
package com.marklogic.appdeployer.cli;

import com.beust.jcommander.Parameters;
import com.marklogic.appdeployer.command.Command;

/**
 * Using this wrapper solely to make "null" go away as the description of a command when calling JCommander's "usage()".
 * <p>
 * Ideally, each Command object can provide a description, but not ready to make every command depend on
 * JCommander annotations.
 */
@Parameters(commandDescription = "")
public class CommandWrapper implements CommandArray {

	private Command command;

	public CommandWrapper(Command command) {
		this.command = command;
	}

	@Override
	public Command[] getCommands() {
		return new Command[]{command};
	}
}
