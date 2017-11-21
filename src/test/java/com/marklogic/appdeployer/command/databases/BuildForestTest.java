package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

public class BuildForestTest extends Assert {

	@Test
	public void databaseAgnosticDirectories() {
		SimplePropertySource source = new SimplePropertySource(
			"mlForestDataDirectory", "/var/data",
			"mlForestFastDataDirectory", "/var/fast",
			"mlForestLargeDataDirectory", "/var/large"
		);
		DefaultAppConfigFactory f = new DefaultAppConfigFactory(source);
		AppConfig config = f.newAppConfig();

		DeployDatabaseCommand command = new DeployDatabaseCommand();
		Forest forest = command.buildForest(config);
		assertEquals("This token is expected to be replaced", "%%FOREST_NAME%%", forest.getForestName());
		assertEquals("This token is expected to be replaced", "%%FOREST_HOST%%", forest.getHost());
		assertEquals("This token is expected to be replaced", "%%FOREST_DATABASE%%", forest.getDatabase());
		assertEquals("/var/data", forest.getDataDirectory());
		assertEquals("/var/fast", forest.getFastDataDirectory());
		assertEquals("/var/large", forest.getLargeDataDirectory());
	}

	@Test
	public void databaseSpecificDirectories() {
		SimplePropertySource source = new SimplePropertySource(
			"mlForestDataDirectory", "/var/data",
			"mlForestFastDataDirectory", "/var/fast",
			"mlForestLargeDataDirectory", "/var/large",
			"mlDatabaseDataDirectories", "my-database,/opt/data",
			"mlDatabaseFastDataDirectories", "my-database,/opt/fast",
			"mlDatabaseLargeDataDirectories", "my-database,/opt/large"
		);
		DefaultAppConfigFactory f = new DefaultAppConfigFactory(source);
		AppConfig config = f.newAppConfig();

		DeployDatabaseCommand command = new DeployDatabaseCommand();
		command.setDatabaseName("my-database");
		Forest forest = command.buildForest(config);
		assertEquals("This token is expected to be replaced", "%%FOREST_NAME%%", forest.getForestName());
		assertEquals("This token is expected to be replaced", "%%FOREST_HOST%%", forest.getHost());
		assertEquals("This token is expected to be replaced", "%%FOREST_DATABASE%%", forest.getDatabase());
		assertEquals("/opt/data", forest.getDataDirectory());
		assertEquals("/opt/fast", forest.getFastDataDirectory());
		assertEquals("/opt/large", forest.getLargeDataDirectory());
	}
}
