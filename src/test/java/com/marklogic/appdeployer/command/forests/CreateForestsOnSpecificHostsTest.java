package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.*;

public class CreateForestsOnSpecificHostsTest extends Assert {

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
		List<String> hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(3, hostNames.size());

		// Select 2 of the 3 hosts for test-db
		Properties props = new Properties();
		props.setProperty("mlDatabaseHosts", "test-db,host1|host2");
		DefaultAppConfigFactory factory = new DefaultAppConfigFactory(new SimplePropertySource(props));
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);

		hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(2, hostNames.size());
		assertTrue(hostNames.contains("host1"));
		assertTrue(hostNames.contains("host2"));

		// Select 1 of the 3 hosts, and include a bad host; the bad one should be ignored
		props.setProperty("mlDatabaseHosts", "some-other-db,host2,test-db,bad-host|host2");
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);
		hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(1, hostNames.size());
		assertTrue(hostNames.contains("host2"));
	}
}
