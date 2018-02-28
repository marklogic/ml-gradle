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
