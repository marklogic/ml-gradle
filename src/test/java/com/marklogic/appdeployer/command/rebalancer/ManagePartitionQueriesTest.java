package com.marklogic.appdeployer.command.rebalancer;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ManagePartitionQueriesTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setAddHostNameTokens(true);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/partition-queries")));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployPartitionsCommand(),
			new DeployPartitionQueriesCommand());

		deploySampleApp();

		try {
			List<String> forestNames = new DatabaseManager(manageClient).getForestNames("sample-app-content");
			assertEquals(3, forestNames.size());
			assertTrue(forestNames.contains("sample-app-content-1"));
			assertTrue(forestNames.contains("tier1-0001"));
			assertTrue(forestNames.contains("tier2-0001"));

			DatabaseClient client = appConfig.newAppServicesDatabaseClient("sample-app-content");
			GenericDocumentManager mgr = client.newDocumentManager();
			assertNotNull(mgr.readAs("http://marklogic.com/xdmp/partitions/1", String.class));
			assertNotNull(mgr.readAs("http://marklogic.com/xdmp/partitions/2", String.class));
		} finally {
			undeploySampleApp();
		}
	}
}
