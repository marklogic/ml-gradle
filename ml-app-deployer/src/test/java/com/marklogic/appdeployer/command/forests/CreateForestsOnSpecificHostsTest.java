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

public class CreateForestsOnSpecificHostsTest  {

	@Test
	public void test() {
		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		DeployForestsCommand command = new DeployForestsCommand("test-db");

		List<String> fakeHostNames = new ArrayList<>();
		fakeHostNames.add("host1");
		fakeHostNames.add("host2");
		fakeHostNames.add("host3");

		command.setHostCalculator(new DefaultHostCalculator(new TestHostNameProvider(fakeHostNames.toArray(new String[]{}))));
		// Verify we get all 3 hosts back when nothing special is configured
		ForestHostNames hostNames = command.determineHostNamesForForest(context, new ArrayList<>());
		assertEquals(3, hostNames.getPrimaryForestHostNames().size());
		assertEquals(3, hostNames.getReplicaForestHostNames().size());

		// Select 2 of the 3 hosts for test-db
		Properties props = new Properties();
		props.setProperty("mlDatabaseHosts", "test-db,host1|host2");
		DefaultAppConfigFactory factory = new DefaultAppConfigFactory(new SimplePropertySource(props));
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);

		hostNames = command.determineHostNamesForForest(context, new ArrayList<>());
		assertEquals(2, hostNames.getPrimaryForestHostNames().size());
		assertEquals(2, hostNames.getReplicaForestHostNames().size());
		assertTrue(hostNames.getPrimaryForestHostNames().contains("host1"));
		assertTrue(hostNames.getPrimaryForestHostNames().contains("host2"));

		// Select 1 of the 3 hosts, and include a bad host; the bad one should be ignored
		props.setProperty("mlDatabaseHosts", "some-other-db,host2,test-db,bad-host|host2");
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);
		hostNames = command.determineHostNamesForForest(context, new ArrayList<>());
		assertEquals(1, hostNames.getPrimaryForestHostNames().size());
		assertEquals(1, hostNames.getReplicaForestHostNames().size());
		assertTrue(hostNames.getPrimaryForestHostNames().contains("host2"));
	}
}
