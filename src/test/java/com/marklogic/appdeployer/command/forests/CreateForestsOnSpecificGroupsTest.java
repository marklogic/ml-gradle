package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class CreateForestsOnSpecificGroupsTest extends Assert {

	@Test
	public void test() {
		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		DeployForestsCommand command = new DeployForestsCommand();
		command.setDatabaseName("test-db");

		List<String> fakeHostNames = new ArrayList<>();
		fakeHostNames.add("name1");
		fakeHostNames.add("name2");
		fakeHostNames.add("name3");
		fakeHostNames.add("name4");
		fakeHostNames.add("name5");

		command.setHostMapProvider(groupName -> {
			if ("group2".equals(groupName)) {
				return buildHostMap("id3,name3");
			}
			if ("group3".equals(groupName)) {
				return buildHostMap("id4,name4,id5,name5");
			}
			return buildHostMap("id1,name1,id2,name2");
		});

		// Verify we get all 5 hosts back when nothing special is configured
		List<String> hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(5, hostNames.size());

		// Select 2 of the 3 hosts for test-db
		Properties props = new Properties();
		props.setProperty("mlDatabaseGroups", "test-db,group1|group2");
		DefaultAppConfigFactory factory = new DefaultAppConfigFactory(new SimplePropertySource(props));
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);

		hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(3, hostNames.size());
		assertTrue(hostNames.contains("name1"));
		assertTrue(hostNames.contains("name2"));
		assertTrue(hostNames.contains("name3"));

		props.setProperty("mlDatabaseGroups", "test-db,group3");
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);
		hostNames = command.determineHostNamesForForest(context, fakeHostNames);
		assertEquals(2, hostNames.size());
		assertTrue(hostNames.contains("name4"));
		assertTrue(hostNames.contains("name5"));
	}

	private Map<String, String> buildHostMap(String str) {
		String[] strings = str.split(",");
		Map<String, String> map = new LinkedHashMap<>();
		for (int i = 0; i < strings.length; i += 2) {
			map.put(strings[i], strings[i + 1]);
		}
		return map;
	}
}
