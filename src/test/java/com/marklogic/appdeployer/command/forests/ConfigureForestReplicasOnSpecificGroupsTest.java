package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigureForestReplicasOnSpecificGroupsTest extends Assert {

	@Test
	public void test() {
		final String dbName = "test-db";

		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();

		Map<String, String> fakeHostMap = new LinkedHashMap<>();
		fakeHostMap.put("id1", "name1");
		fakeHostMap.put("id2", "name2");
		fakeHostMap.put("id3", "name3");
		fakeHostMap.put("id4", "name4");
		fakeHostMap.put("id5", "name5");

		command.setGroupHostMapProvider(groupName -> {
			if ("group2".equals(groupName)) {
				return buildHostMap("id3,name3");
			}
			if ("group3".equals(groupName)) {
				return buildHostMap("id4,name4,id5,name5");
			}
			return buildHostMap("id1,name1,id2,name2");
		});

		// Verify we get all 5 hosts back when nothing special is configured
		List<String> hostIds = command.getHostIdsForDatabaseForests(dbName, fakeHostMap, context);
		assertEquals(5, hostIds.size());

		// Select 2 of the 3 hosts for test-db
		Properties props = new Properties();
		props.setProperty("mlDatabaseGroups", "test-db,group1|group2");
		DefaultAppConfigFactory factory = new DefaultAppConfigFactory(new SimplePropertySource(props));
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);

		hostIds = command.getHostIdsForDatabaseForests(dbName, fakeHostMap, context);
		assertEquals(3, hostIds.size());
		assertTrue(hostIds.contains("id1"));
		assertTrue(hostIds.contains("id2"));
		assertTrue(hostIds.contains("id3"));

		props.setProperty("mlDatabaseGroups", "test-db,group3");
		appConfig = factory.newAppConfig();
		context = new CommandContext(appConfig, null, null);
		hostIds = command.getHostIdsForDatabaseForests(dbName, fakeHostMap, context);
		assertEquals(2, hostIds.size());
		assertTrue(hostIds.contains("id4"));
		assertTrue(hostIds.contains("id5"));
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
