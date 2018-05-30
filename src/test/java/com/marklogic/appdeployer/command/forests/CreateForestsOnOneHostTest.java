package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.CommandContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateForestsOnOneHostTest extends Assert {

	@Test
	public void test() {
		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		DeployForestsCommand command = new DeployForestsCommand("test-db");

		List<String> fakeHostNames = new ArrayList<>();
		fakeHostNames.add("host1");
		fakeHostNames.add("host2");
		fakeHostNames.add("host3");

		HostCalculator hostCalculator = new DefaultHostCalculator(new TestHostNameProvider("host1", "host2", "host3"));
		command.setHostCalculator(hostCalculator);

		List<String> hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(3, hostNames.size());

		command.setCreateForestsOnEachHost(false);
		hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(1, hostNames.size());
		assertEquals("host1", hostNames.get(0));

		command.setCreateForestsOnEachHost(true);
		hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(3, hostNames.size());

		Set<String> names = new HashSet<>();
		names.add("test-db");
		appConfig.setDatabasesWithForestsOnOneHost(names);
		hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(1, hostNames.size());
	}
}
