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
