/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateForestsOnOneHostTest {

	@Test
	public void test() {
		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		DeployForestsCommand command = new DeployForestsCommand("test-db");

		HostCalculator hostCalculator = new DefaultHostCalculator(new TestHostNameProvider("host1", "host2", "host3"));
		command.setHostCalculator(hostCalculator);

		ForestHostNames hostNames = command.determineHostNamesForForest(context, new ArrayList<>());
		assertEquals(3, hostNames.getPrimaryForestHostNames().size());

		appConfig.addDatabaseWithForestsOnOneHost("test-db");
		hostNames = command.determineHostNamesForForest(context, new ArrayList<>());
		assertEquals(1, hostNames.getPrimaryForestHostNames().size());
	}

	/**
	 * Added for ticket #439, where the wrong host was being selected when forests already exist on a host that isn'
	 * the first one in the list.
	 */
	@Test
	void secondHostAlreadyHasForests() {
		DeployForestsCommand command = new DeployForestsCommand("test-db");
		command.setHostCalculator(new DefaultHostCalculator(new TestHostNameProvider("host1", "host2", "host3")));

		AppConfig appConfig = new DefaultAppConfigFactory(new SimplePropertySource(
			"mlDatabasesWithForestsOnOneHost", "test-db",
			"mlForestsPerHost", "test-db,2"
		)).newAppConfig();

		CommandContext context = new CommandContext(appConfig, null, null);

		final List<Forest> existingForests = Arrays.asList(new Forest("host2", "test-db-1"));
		ForestHostNames hostNames = command.determineHostNamesForForest(context, existingForests);

		assertEquals(1, hostNames.getPrimaryForestHostNames().size(), "Only one host name should exist since the " +
			"database should only have forests on one host");
		assertEquals("host2", hostNames.getPrimaryForestHostNames().get(0), "Because there's already a forest on " +
			"host2, that should be the primary forest host name, even though host1 is first in the list");

		List<Forest> forestsToCreate = command.buildForests(context, existingForests);
		assertEquals(1, forestsToCreate.size(), "Because forests per host is 2 and host2 already has 1 forest, 1 more " +
			"forest needs to be created");
		assertEquals("host2", forestsToCreate.get(0).getHost());
		assertEquals("test-db-2", forestsToCreate.get(0).getForestName());
	}

	@Test
	void forestsShouldGoOnHostInSecondGroup() {
		DeployForestsCommand command = new DeployForestsCommand("test-db");
		TestHostNameProvider hostNameProvider = new TestHostNameProvider();
		hostNameProvider.addGroupHostNames("group1", "host1-1", "host1-2");
		hostNameProvider.addGroupHostNames("group2", "host2-1", "host2-2");
		command.setHostCalculator(new DefaultHostCalculator(hostNameProvider));

		AppConfig appConfig = new DefaultAppConfigFactory(new SimplePropertySource(
			"mlDatabasesWithForestsOnOneHost", "test-db",
			"mlDatabaseGroups", "test-db,group2"
		)).newAppConfig();

		CommandContext context = new CommandContext(appConfig, null, null);

		ForestHostNames hostNames = command.determineHostNamesForForest(context, new ArrayList<>());
		assertEquals(1, hostNames.getPrimaryForestHostNames().size());
		assertEquals("host2-1", hostNames.getPrimaryForestHostNames().get(0), "mlDatabaseGroups says that the " +
			"database should only have forests on hosts in group2, so host2-1 - the first host in group2 - should " +
			"be selected");
	}

}
