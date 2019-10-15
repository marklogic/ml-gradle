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

	private ForestBuilder builder = new ForestBuilder();

	@Test
	public void multipleForestsOnEachHost() {
		AppConfig appConfig = newAppConfig("mlForestsPerHost", "db,2");

		List<Forest> forests = builder.buildForests(
			new ForestPlan("db", "host1", "host2", "host3").withReplicaCount(1), appConfig);

		assertEquals("host1", forests.get(0).getHost());
		assertEquals("host2", forests.get(0).getForestReplica().get(0).getHost());
		assertEquals("host1", forests.get(1).getHost());
		assertEquals("host3", forests.get(1).getForestReplica().get(0).getHost());

		assertEquals("host2", forests.get(2).getHost());
		assertEquals("host3", forests.get(2).getForestReplica().get(0).getHost());
		assertEquals("host2", forests.get(3).getHost());
		assertEquals("host1", forests.get(3).getForestReplica().get(0).getHost());

		assertEquals("host3", forests.get(4).getHost());
		assertEquals("host1", forests.get(4).getForestReplica().get(0).getHost());
		assertEquals("host3", forests.get(5).getHost());
		assertEquals("host2", forests.get(5).getForestReplica().get(0).getHost());
	}

	@Test
	public void customNamingStrategyWithDistributedStrategy() {
		AppConfig appConfig = newAppConfig("mlForestsPerHost", "my-database,2");
		addCustomNamingStrategy(appConfig);

		List<Forest> forests = builder.buildForests(
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

	@Test
	public void hostIsAdded() {
		AppConfig appConfig = newAppConfig();
		List<Forest> forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2", "host3")
			.withForestsPerDataDirectory(1).withReplicaCount(1), appConfig);
		assertEquals(3, forests.size());
		assertEquals("testdb-1-replica-1", forests.get(0).getForestReplica().get(0).getReplicaName());
		assertEquals("host2", forests.get(0).getForestReplica().get(0).getHost());
		assertEquals("testdb-2-replica-1", forests.get(1).getForestReplica().get(0).getReplicaName());
		assertEquals("host3", forests.get(1).getForestReplica().get(0).getHost());
		assertEquals("testdb-3-replica-1", forests.get(2).getForestReplica().get(0).getReplicaName());
		assertEquals("host1", forests.get(2).getForestReplica().get(0).getHost());

		forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2", "host3", "host4")
			.withForestsPerDataDirectory(1).withExistingForests(forests).withReplicaCount(1), appConfig);
		assertEquals(1, forests.size());
		assertEquals("testdb-4-replica-1", forests.get(0).getForestReplica().get(0).getReplicaName());
		assertEquals(
			"When adding a new host and creating replicas, the replicas will naturally be uneven because the existing " +
				"forests on hosts 1, 2, and 3 won't have their replicas moved to 4 automatically, and thus host 4 won't " +
				"have any replicas on it. And for the new replicas, we expect those to start being created on the first host.",
			"host1", forests.get(0).getForestReplica().get(0).getHost());
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

		List<Forest> forests = builder.buildForests(
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

	@Test
	public void multipleReplicaDataDirectories() {
		AppConfig config = newAppConfig("mlDatabaseReplicaDataDirectories", "my-database,/path1|/path2");

		List<Forest> forests = builder.buildForests(
			new ForestPlan("my-database", "host1", "host2", "host3").withReplicaCount(2), config);

		Forest f1 = forests.get(0);
		assertEquals("my-database-1", f1.getForestName());
		assertEquals("my-database-1-replica-1", f1.getForestReplica().get(0).getReplicaName());
		assertEquals("host2", f1.getForestReplica().get(0).getHost());
		assertEquals("/path1", f1.getForestReplica().get(0).getDataDirectory());
		assertEquals("my-database-1-replica-2", f1.getForestReplica().get(1).getReplicaName());
		assertEquals("host3", f1.getForestReplica().get(1).getHost());
		assertEquals("/path2", f1.getForestReplica().get(1).getDataDirectory());

		Forest f2 = forests.get(1);
		assertEquals("my-database-2", f2.getForestName());
		assertEquals("my-database-2-replica-1", f2.getForestReplica().get(0).getReplicaName());
		assertEquals("host3", f2.getForestReplica().get(0).getHost());
		assertEquals("/path1", f2.getForestReplica().get(0).getDataDirectory());
		assertEquals("my-database-2-replica-2", f2.getForestReplica().get(1).getReplicaName());
		assertEquals("host1", f2.getForestReplica().get(1).getHost());
		assertEquals("/path2", f2.getForestReplica().get(1).getDataDirectory());

		Forest f3 = forests.get(2);
		assertEquals("my-database-3", f3.getForestName());
		assertEquals("my-database-3-replica-1", f3.getForestReplica().get(0).getReplicaName());
		assertEquals("host1", f3.getForestReplica().get(0).getHost());
		assertEquals("/path1", f3.getForestReplica().get(0).getDataDirectory());
		assertEquals("my-database-3-replica-2", f3.getForestReplica().get(1).getReplicaName());
		assertEquals("host2", f3.getForestReplica().get(1).getHost());
		assertEquals("/path2", f3.getForestReplica().get(1).getDataDirectory());

//		for (Forest forest : forests) {
//			System.out.println(forest.getForestName());
//			for (ForestReplica replica : forest.getForestReplica()) {
//				System.out.println(replica.getHost() + ":" + replica.getDataDirectory());
//			}
//		}
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

		Forest forest = builder.buildForests(
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
