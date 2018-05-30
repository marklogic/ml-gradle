package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ConfigureForestReplicasOnSpecificGroupsTest extends Assert {

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
