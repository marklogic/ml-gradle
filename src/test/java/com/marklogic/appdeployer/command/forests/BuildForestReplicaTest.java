package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.mgmt.api.forest.ForestReplica;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

public class BuildForestReplicaTest extends Assert {

	@Test
	public void databaseAgnosticDirectories() {
		ForestReplica replica = buildForestReplica(
			"mlForestDataDirectory", "/var/data",
			"mlForestFastDataDirectory", "/var/fast",
			"mlForestLargeDataDirectory", "/var/large"
		);

		assertEquals("/var/data", replica.getDataDirectory());
		assertEquals("/var/fast", replica.getFastDataDirectory());
		assertEquals("/var/large", replica.getLargeDataDirectory());
	}

	@Test
	public void databaseSpecificDirectories() {
		ForestReplica replica = buildForestReplica(
			"mlForestDataDirectory", "/var/data",
			"mlForestFastDataDirectory", "/var/fast",
			"mlForestLargeDataDirectory", "/var/large",
			"mlDatabaseDataDirectories", "my-database,/my/data",
			"mlDatabaseFastDataDirectories", "my-database,/my/fast",
			"mlDatabaseLargeDataDirectories", "my-database,/my/large"
		);

		assertEquals("/my/data", replica.getDataDirectory());
		assertEquals("/my/fast", replica.getFastDataDirectory());
		assertEquals("/my/large", replica.getLargeDataDirectory());
	}

	@Test
	public void databaseAgnosticReplicaDirectories() {
		ForestReplica replica = buildForestReplica(
			"mlDatabaseDataDirectories", "my-database,/my/data",
			"mlDatabaseFastDataDirectories", "my-database,/my/fast",
			"mlDatabaseLargeDataDirectories", "my-database,/my/large",
			"mlForestDataDirectory", "/var/data",
			"mlForestFastDataDirectory", "/var/fast",
			"mlForestLargeDataDirectory", "/var/large",
			"mlReplicaForestDataDirectory", "/replica/data",
			"mlReplicaForestFastDataDirectory", "/replica/fast",
			"mlReplicaForestLargeDataDirectory", "/replica/large"
		);

		assertEquals("/replica/data", replica.getDataDirectory());
		assertEquals("/replica/fast", replica.getFastDataDirectory());
		assertEquals("/replica/large", replica.getLargeDataDirectory());
	}

	@Test
	public void databaseSpecificReplicaDirectories() {
		ForestReplica replica = buildForestReplica(
			"mlForestDataDirectory", "/var/data",
			"mlForestFastDataDirectory", "/var/fast",
			"mlForestLargeDataDirectory", "/var/large",
			"mlDatabaseDataDirectories", "my-database,/my/data",
			"mlDatabaseFastDataDirectories", "my-database,/my/fast",
			"mlDatabaseLargeDataDirectories", "my-database,/my/large",
			"mlReplicaForestDataDirectory", "/replica/data",
			"mlReplicaForestFastDataDirectory", "/replica/fast",
			"mlReplicaForestLargeDataDirectory", "/replica/large",
			"mlDatabaseReplicaDataDirectories", "my-database,/my/replica/data",
			"mlDatabaseReplicaFastDataDirectories", "my-database,/my/replica/fast",
			"mlDatabaseReplicaLargeDataDirectories", "my-database,/my/replica/large"
		);

		assertEquals("/my/replica/data", replica.getDataDirectory());
		assertEquals("/my/replica/fast", replica.getFastDataDirectory());
		assertEquals("/my/replica/large", replica.getLargeDataDirectory());
	}

	private ForestReplica buildForestReplica(String... propertyNamesAndValues) {
		SimplePropertySource source = new SimplePropertySource(propertyNamesAndValues);
		DefaultAppConfigFactory f = new DefaultAppConfigFactory(source);
		AppConfig config = f.newAppConfig();

		ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();
		ForestReplica replica = command.buildForestReplica("my-database", "test-name", "host-1", config);
		assertEquals("test-name", replica.getReplicaName());
		assertEquals("host-1", replica.getHost());
		return replica;
	}
}
