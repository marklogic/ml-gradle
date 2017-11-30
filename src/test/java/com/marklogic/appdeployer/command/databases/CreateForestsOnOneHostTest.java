package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.forests.DeployForestsCommand;
import com.marklogic.mgmt.SaveReceipt;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CreateForestsOnOneHostTest extends Assert {

	@Test
	public void test() {
		DeployDatabaseCommand command = new DeployDatabaseCommand();

		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		Set<String> names = new HashSet<>();
		names.add("some-name");
		appConfig.setDatabaseNamesWithForestsOnOneHost(names);

		Map<String, Integer> forestCountMap = new HashMap<>();
		forestCountMap.put("some-name", 3);
		forestCountMap.put("other-name", 2);
		appConfig.setForestCounts(forestCountMap);

		String payload = "{\"database-name\":\"some-name\"}";
		SaveReceipt receipt = new SaveReceipt("some-name", payload, null, null);
		DeployForestsCommand forestsCommand = command.buildDeployForestsCommand(payload, receipt, context);
		assertFalse(forestsCommand.isCreateForestsOnEachHost());
		assertEquals(3, forestsCommand.getForestsPerHost());

		payload = "{\"database-name\":\"other-name\"}";
		receipt = new SaveReceipt("other-name", payload, null, null);
		forestsCommand = command.buildDeployForestsCommand(payload, receipt, context);
		assertTrue(forestsCommand.isCreateForestsOnEachHost());
		assertEquals(2, forestsCommand.getForestsPerHost());
	}
}
