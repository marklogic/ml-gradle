package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.api.forest.ForestReplica;
import org.junit.Assert;
import org.junit.Test;

public class BuildForestReplicaTest extends Assert {

	/**
	 * Small unit test to verify that a ForestReplica is built correctly based on the AppConfig.ø
	 */
	@Test
	public void test() {
		AppConfig config = new AppConfig();
		config.setReplicaForestFastDataDirectory("/var/fast");
		config.setReplicaForestLargeDataDirectory("/var/large");
		config.setReplicaForestDataDirectory("/var/data");
		CommandContext context = new CommandContext(config, null, null);

		ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();
		ForestReplica replica = command.buildForestReplica("test-name", "host-1", config);
		assertEquals("test-name", replica.getReplicaName());
		assertEquals("host-1", replica.getHost());
		assertEquals("/var/data", replica.getDataDirectory());
		assertEquals("/var/fast", replica.getFastDataDirectory());
		assertEquals("/var/large", replica.getLargeDataDirectory());
	}
}
