package com.marklogic.appdeployer.cli;

import com.marklogic.appdeployer.command.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeployCommand implements CommandArray {

	private Map<String, List<Command>> commandMap;

	public DeployCommand(Map<String, List<Command>> commandMap) {
		this.commandMap = commandMap;
	}

	@Override
	public Command[] getCommands() {
		List<Command> list = new ArrayList<>();
		for (String group : commandMap.keySet()) {
			list.addAll(commandMap.get(group));
		}
		return list.toArray(new Command[]{});
	}
}
