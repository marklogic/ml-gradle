package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BuildForestReplicaTest extends Assert {

	@Test
	public void customNamingStrategyWithDistributedStrategy() {
		AppConfig appConfig = newAppConfig("mlForestsPerHost", "my-database,2");
		addCustomNamingStrategy(appConfig);

		List<Forest> forests = new ForestBuilder().buildForests(
			new ForestPlan("my-database", "host1", "host2", "host3").withReplicaCount(2), appConfig);

		Forest f1 = forests.get(0);
		assertEquals("forest-1", f1.getForestName());
		assertEquals("host1", f1.getHost());
		assertEquals("my-replica-forest-1-1", f1.getForestReplica().get(0).getReplicaName());
		assertEquals("my-replica-forest-1-2", f1.getForestReplica().get(1).getReplicaName());
		assertEquals("host2", f1.getForestReplica().get(0).getHost());
		assertEquals("host3", f1.getForestReplica().get(1).getHost());

		Forest f2 = forests.get(1);
		assertEquals("forest-2", f2.getForestName());
		assertEquals("host1", f2.getHost());
		assertEquals("my-replica-forest-2-1", f2.getForestReplica().get(0).getReplicaName());
		assertEquals("my-replica-forest-2-2", f2.getForestReplica().get(1).getReplicaName());
		assertEquals("host3", f2.getForestReplica().get(0).getHost());
		assertEquals("host2", f2.getForestReplica().get(1).getHost());

		Forest f3 = forests.get(2);
		assertEquals("forest-3", f3.getForestName());
		assertEquals("host2", f3.getHost());
		assertEquals("my-replica-forest-3-1", f3.getForestReplica().get(0).getReplicaName());
		assertEquals("my-replica-forest-3-2", f3.getForestReplica().get(1).getReplicaName());
		assertEquals("host3", f3.getForestReplica().get(0).getHost());
		assertEquals("host1", f3.getForestReplica().get(1).getHost());

		Forest f5 = forests.get(4);
		assertEquals("forest-5", f5.getForestName());
		assertEquals("host3", f5.getHost());
		assertEquals("my-replica-forest-5-1", f5.getForestReplica().get(0).getReplicaName());
		assertEquals("my-replica-forest-5-2", f5.getForestReplica().get(1).getReplicaName());
		assertEquals("host1", f5.getForestReplica().get(0).getHost());
		assertEquals("host2", f5.getForestReplica().get(1).getHost());
	}

	/**
	 * This shows how replicas for host 1 all end up on host 2. The distributed strategy above is preferred, and
	 * the grouped one is deprecated, but this test exists to show how the grouped one differs while it still exists.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void customNamingStrategyWithGroupedStrategy() {
		AppConfig appConfig = newAppConfig("mlForestsPerHost", "my-database,2");
		addCustomNamingStrategy(appConfig);
		appConfig.setReplicaBuilderStrategy(new GroupedReplicaBuilderStrategy());

		List<Forest> forests = new ForestBuilder().buildForests(
			new ForestPlan("my-database", "host1", "host2", "host3").withReplicaCount(2), appConfig);

		Forest f1 = forests.get(0);
		assertEquals("forest-1", f1.getForestName());
		assertEquals("host1", f1.getHost());
		assertEquals("my-replica-forest-1-1", f1.getForestReplica().get(0).getReplicaName());
		assertEquals("my-replica-forest-1-2", f1.getForestReplica().get(1).getReplicaName());
		assertEquals("host2", f1.getForestReplica().get(0).getHost());
		assertEquals("host3", f1.getForestReplica().get(1).getHost());

		Forest f2 = forests.get(1);
		assertEquals("forest-2", f2.getForestName());
		assertEquals("host1", f2.getHost());
		assertEquals("my-replica-forest-2-1", f2.getForestReplica().get(0).getReplicaName());
		assertEquals("my-replica-forest-2-2", f2.getForestReplica().get(1).getReplicaName());
		assertEquals("host2", f2.getForestReplica().get(0).getHost());
		assertEquals("host3", f2.getForestReplica().get(1).getHost());
	}

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

	private void addCustomNamingStrategy(AppConfig appConfig) {
		appConfig.getForestNamingStrategies().put("my-database", new ForestNamingStrategy() {
			@Override
			public String getForestName(String databaseName, int forestNumber, AppConfig appConfig) {
				return "forest-" + forestNumber;
			}

			@Override
			public String getReplicaName(String databaseName, String forestName, int forestReplicaNumber, AppConfig appConfig) {
				return "my-replica-" + forestName + "-" + forestReplicaNumber;
			}
		});
	}

	private ForestReplica buildForestReplica(String... propertyNamesAndValues) {
		AppConfig config = newAppConfig(propertyNamesAndValues);

		Forest forest = new ForestBuilder().buildForests(
			new ForestPlan("my-database", "host1", "host2").withReplicaCount(1), config).get(0);
		ForestReplica replica = forest.getForestReplica().get(0);
		assertEquals("my-database-1-replica-1", replica.getReplicaName());
		assertEquals("host2", replica.getHost());
		return replica;
	}

	protected AppConfig newAppConfig(String... propertyNamesAndValues) {
		SimplePropertySource source = new SimplePropertySource(propertyNamesAndValues);
		DefaultAppConfigFactory f = new DefaultAppConfigFactory(source);
		return f.newAppConfig();
	}
}
