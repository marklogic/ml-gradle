/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;
import com.marklogic.mgmt.util.SimplePropertySource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildForestTest  {

	private ForestBuilder builder = new ForestBuilder();

	@Test
	public void customNamingStrategy() {
		AppConfig appConfig = new AppConfig();
		appConfig.getForestNamingStrategies().put("testdb", new ForestNamingStrategy() {
			@Override
			public String getForestName(String databaseName, int forestNumber, AppConfig appConfig) {
				return "custom-" + databaseName + "-" + forestNumber;
			}

			@Override
			public String getReplicaName(String databaseName, String forestName, int forestReplicaNumber, AppConfig appConfig) {
				return null;
			}
		});
		appConfig.getForestNamingStrategies().put("shouldn't be used", new DefaultForestNamingStrategy());

		ForestPlan plan = new ForestPlan("testdb", "host1", "host2");
		List<Forest> forests = builder.buildForests(plan, appConfig);
		verifyNameAndHost(forests.get(0), "custom-testdb-1", "host1");
		verifyNameAndHost(forests.get(1), "custom-testdb-2", "host2");
	}

	@Test
	public void withTemplate() {
		ForestPlan plan = new ForestPlan("testdb", "host1", "host2");
		plan.withTemplate("{\"rebalancer-enable\":false, \"failover-enable\":true}");

		List<Forest> forests = builder.buildForests(plan, new AppConfig());
		verifyNameAndHost(forests.get(0), "testdb-1", "host1");
		assertFalse(forests.get(0).isRebalancerEnable());
		assertTrue(forests.get(0).getFailoverEnable());

		verifyNameAndHost(forests.get(1), "testdb-2", "host2");
		assertFalse(forests.get(1).isRebalancerEnable());
		assertTrue(forests.get(1).getFailoverEnable());
	}

	@Test
	public void multipleDataDirectoriesWithReplicas() {
		SimplePropertySource source = new SimplePropertySource(
			"mlDatabaseDataDirectories", "testdb,/dir1|/dir2|/dir3"
		);
		AppConfig config = new DefaultAppConfigFactory(source).newAppConfig();

		List<Forest> forests = builder.buildForests(
			new ForestPlan("testdb", "host1", "host2", "host3").withReplicaCount(2).withForestsPerDataDirectory(2), config
		);
		assertEquals(18, forests.size(),
			"Should have 18 forests - 3 data directories, and 2 forests per data directory, and 3 hosts");

		Forest first = forests.get(0);
		assertEquals("testdb-1", first.getForestName());
		assertEquals("host1", first.getHost());
		assertEquals("testdb", first.getDatabase());
		assertEquals("/dir1", first.getDataDirectory());
		assertEquals(2, first.getForestReplica().size());
		ForestReplica r1 = first.getForestReplica().get(0);
		assertEquals("host2", r1.getHost());
		assertEquals("testdb-1-replica-1", r1.getReplicaName());
		assertEquals("/dir2", r1.getDataDirectory());
		ForestReplica r2 = first.getForestReplica().get(1);
		assertEquals("host3", r2.getHost());
		assertEquals("testdb-1-replica-2", r2.getReplicaName());
		assertEquals("/dir3", r2.getDataDirectory());
	}

	@Test
	public void someForestsAlreadyExist() {
		AppConfig config = new DefaultAppConfigFactory().newAppConfig();
		List<Forest> forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2", "host3").withForestsPerDataDirectory(1), config);
		assertEquals(3, forests.size());
		verifyNameAndHost(forests.get(0), "testdb-1", "host1");
		verifyNameAndHost(forests.get(1), "testdb-2", "host2");
		verifyNameAndHost(forests.get(2), "testdb-3", "host3");

		forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2", "host3")
			.withForestsPerDataDirectory(3).withExistingForests(forests), config);
		assertEquals(6, forests.size());
		verifyNameAndHost(forests.get(0), "testdb-4", "host1");
		verifyNameAndHost(forests.get(1), "testdb-5", "host1");
		verifyNameAndHost(forests.get(2), "testdb-6", "host2");
		verifyNameAndHost(forests.get(3), "testdb-7", "host2");
		verifyNameAndHost(forests.get(4), "testdb-8", "host3");
		verifyNameAndHost(forests.get(5), "testdb-9", "host3");
	}

	@Test
	public void twoDataDirectories() {
		AppConfig config = new DefaultAppConfigFactory().newAppConfig();
		Map<String, List<String>> dataDirectories = new HashMap<>();
		dataDirectories.put("testdb", Arrays.asList("/path1", "/path2"));
		config.setDatabaseDataDirectories(dataDirectories);

		List<Forest> forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2", "host3").withForestsPerDataDirectory(1), config);
		assertEquals(6, forests.size());
		verifyNameAndHost(forests.get(0), "testdb-1", "host1");
		verifyNameAndHost(forests.get(1), "testdb-2", "host1");
		verifyNameAndHost(forests.get(2), "testdb-3", "host2");
		verifyNameAndHost(forests.get(3), "testdb-4", "host2");
		verifyNameAndHost(forests.get(4), "testdb-5", "host3");
		verifyNameAndHost(forests.get(5), "testdb-6", "host3");

		forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2", "host3")
			.withForestsPerDataDirectory(2).withExistingForests(forests), config);
		assertEquals(6, forests.size());
		verifyNameAndHost(forests.get(0), "testdb-7", "host1");
		verifyNameAndHost(forests.get(1), "testdb-8", "host1");
		verifyNameAndHost(forests.get(2), "testdb-9", "host2");
		verifyNameAndHost(forests.get(3), "testdb-10", "host2");
		verifyNameAndHost(forests.get(4), "testdb-11", "host3");
		verifyNameAndHost(forests.get(5), "testdb-12", "host3");
	}

	@Test
	public void hostIsAdded() {
		AppConfig config = new DefaultAppConfigFactory().newAppConfig();
		List<Forest> forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2").withForestsPerDataDirectory(1), config);
		assertEquals(2, forests.size());
		verifyNameAndHost(forests.get(0), "testdb-1", "host1");
		verifyNameAndHost(forests.get(1), "testdb-2", "host2");

		forests = builder.buildForests(new ForestPlan("testdb", "host1", "host2", "host3")
			.withForestsPerDataDirectory(2).withExistingForests(forests), config);
		assertEquals(4, forests.size());
		verifyNameAndHost(forests.get(0), "testdb-3", "host1");
		verifyNameAndHost(forests.get(1), "testdb-4", "host2");
		verifyNameAndHost(forests.get(2), "testdb-5", "host3");
		verifyNameAndHost(forests.get(3), "testdb-6", "host3");
	}

	@Test
	public void databaseAgnosticDirectories() {
		SimplePropertySource source = new SimplePropertySource(
			"mlForestDataDirectory", "/var/data",
			"mlForestFastDataDirectory", "/var/fast",
			"mlForestLargeDataDirectory", "/var/large"
		);
		DefaultAppConfigFactory f = new DefaultAppConfigFactory(source);
		AppConfig config = f.newAppConfig();

		Forest forest = builder.buildForests(new ForestPlan("testdb", "host1"), config).get(0);
		assertEquals("testdb-1", forest.getForestName());
		assertEquals("host1", forest.getHost());
		assertEquals("testdb", forest.getDatabase());
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

		Forest forest = builder.buildForests(new ForestPlan("my-database", "host1"), config).get(0);
		assertEquals("my-database-1", forest.getForestName());
		assertEquals("host1", forest.getHost());
		assertEquals("my-database", forest.getDatabase());
		assertEquals("/opt/data", forest.getDataDirectory());
		assertEquals("/opt/fast", forest.getFastDataDirectory());
		assertEquals("/opt/large", forest.getLargeDataDirectory());
	}

	private void verifyNameAndHost(Forest f, String name, String host) {
		assertEquals(name, f.getForestName());
		assertEquals(host, f.getHost());
	}
}
