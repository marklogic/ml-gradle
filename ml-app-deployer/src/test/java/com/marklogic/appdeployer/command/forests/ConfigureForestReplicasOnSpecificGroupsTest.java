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
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.util.SimplePropertySource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ConfigureForestReplicasOnSpecificGroupsTest  {

	@Test
	public void test() {
		final String dbName = "test-db";

		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();

		List<String> fakeHostNames = Arrays.asList("name1", "name2", "name3", "name4", "name5");

		command.setGroupHostNamesProvider(groupName -> {
			if ("group2".equals(groupName)) {
				return Arrays.asList("name3");
			}
			if ("group3".equals(groupName)) {
				return Arrays.asList("name4","name5");
			}
			return Arrays.asList("name1","name2");
		});

		// Verify we get all 5 hosts back when nothing special is configured
		List<String> hostNames = command.getHostNamesForDatabaseForests(dbName, fakeHostNames, context);
		assertEquals(5, hostNames.size());

		// Select 2 of the 3 hosts for test-db
		Properties props = new Properties();
		props.setProperty("mlDatabaseGroups", "test-db,group1|group2");
		DefaultAppConfigFactory factory = new DefaultAppConfigFactory(new SimplePropertySource(props));
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);

		hostNames = command.getHostNamesForDatabaseForests(dbName, fakeHostNames, context);
		assertEquals(3, hostNames.size());
		assertTrue(hostNames.contains("name1"));
		assertTrue(hostNames.contains("name2"));
		assertTrue(hostNames.contains("name3"));

		props.setProperty("mlDatabaseGroups", "test-db,group3");
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);
		hostNames = command.getHostNamesForDatabaseForests(dbName, fakeHostNames, context);
		assertEquals(2, hostNames.size());
		assertTrue(hostNames.contains("name4"));
		assertTrue(hostNames.contains("name5"));
	}
}
