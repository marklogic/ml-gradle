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
package com.marklogic.appdeployer.command;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CommandMapBuilderTest {

	CommandMapBuilder commandMapBuilder = new CommandMapBuilder();

	@Test
	void buildCommandsForNonReplicaCluster() {
		Map<String, List<Command>> map = commandMapBuilder.buildCommandMap();
		verifyCommandsThatDoNotWriteDataExist(map);
		verifyExistenceOfCommandsThatDoWriteData(map, true);
	}

	@Test
	void buildCommandsForReplicaCluster() {
		Map<String, List<Command>> map = commandMapBuilder.buildCommandMapForReplicaCluster();
		verifyCommandsThatDoNotWriteDataExist(map);
		verifyExistenceOfCommandsThatDoWriteData(map, false);

		List<Command> mapCommands = map.values().stream().reduce(new ArrayList<>(), (a, b) -> {a.addAll(b); return a;});

		List<Command> commands = commandMapBuilder.getCommandsForReplicaCluster();
		assertEquals(mapCommands.size(), commands.size(), "The convenience method for getting a list of commands " +
			"should have the same number of commands as the map of commands that we just validated");
	}

	private void verifyCommandsThatDoNotWriteDataExist(Map<String, List<Command>> map) {
		Stream.of("mlClusterCommands", "mlConfigurationCommands", "mlDatabaseCommands", "mlForestCommands", "mlForestReplicaCommands",
			"mlGroupCommands", "mlHostCommands", "mlMimetypeCommands", "mlPluginCommands", "mlRebalancerCommands",
			"mlRestApiCommands", "mlSecurityCommands", "mlServerCommands", "mlTaskCommands"
		).forEach(groupName -> {
			assertTrue(map.containsKey(groupName));
		});
	}

	private void verifyExistenceOfCommandsThatDoWriteData(Map<String, List<Command>> map, boolean shouldExist) {
		Stream.of("mlAlertCommands", "mlCpfCommands", "mlDataCommands", "mlFlexrepCommands", "mlModuleCommands",
			"mlSchemaCommands", "mlTemporalCommands", "mlTriggerCommands", "mlViewCommands"
		).forEach(groupName -> {
			if (shouldExist) {
				assertTrue(map.containsKey(groupName),
					"When deploying to a non-replica cluster (i.e. one that is not the target for database replication " +
						"from a master cluster), it is safe to execute commands that write data to a database");
			} else {
				assertFalse(map.containsKey(groupName),
					"When deploying to a replica cluster, a command that writes data to a database should not be " +
						"included in the list of commands to execute");
			}
		});
	}
}
