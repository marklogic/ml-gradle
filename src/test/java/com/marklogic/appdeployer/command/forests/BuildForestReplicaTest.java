package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.mgmt.api.forest.ForestReplica;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

public class BuildForestReplicaTest extends Assert {

	@Test
	public void databaseAgnosticReplicaDirectories() {
		AppConfig config = new AppConfig();
		config.setReplicaForestFastDataDirectory("/var/fast");
		config.setReplicaForestLargeDataDirectory("/var/large");
		config.setReplicaForestDataDirectory("/var/data");

		ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();
		ForestReplica replica = command.buildForestReplica(null, "test-name", "host-1", config);
		assertEquals("test-name", replica.getReplicaName());
		assertEquals("host-1", replica.getHost());
		assertEquals("/var/data", replica.getDataDirectory());
		assertEquals("/var/fast", replica.getFastDataDirectory());
		assertEquals("/var/large", replica.getLargeDataDirectory());
	}

	@Test
	public void databaseSpecificReplicaDirectories() {
		SimplePropertySource source = new SimplePropertySource(
			"mlReplicaForestDataDirectory", "/var/data",
			"mlReplicaForestFastDataDirectory", "/var/fast",
			"mlReplicaForestLargeDataDirectory", "/var/large",
			"mlDatabaseReplicaDataDirectories", "my-database,/opt/data",
			"mlDatabaseReplicaFastDataDirectories", "my-database,/opt/fast",
			"mlDatabaseReplicaLargeDataDirectories", "my-database,/opt/large"
		);
		DefaultAppConfigFactory f = new DefaultAppConfigFactory(source);
		AppConfig config = f.newAppConfig();

		ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();
		ForestReplica replica = command.buildForestReplica("my-database", "test-name", "host-1", config);
		assertEquals("test-name", replica.getReplicaName());
		assertEquals("host-1", replica.getHost());
		assertEquals("/opt/data", replica.getDataDirectory());
		assertEquals("/opt/fast", replica.getFastDataDirectory());
		assertEquals("/opt/large", replica.getLargeDataDirectory());
	}
}
