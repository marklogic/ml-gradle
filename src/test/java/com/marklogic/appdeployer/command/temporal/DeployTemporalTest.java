package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionManager;
import com.marklogic.rest.util.ResourcesFragment;
import org.junit.Test;

import java.io.File;

public class DeployTemporalTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/temporal-config-with-lsqt"));

		initializeAppDeployer(new DeployContentDatabasesCommand(1),
			new DeploySchemasDatabaseCommand(), new DeployTemporalAxesCommand(),
			new DeployTemporalCollectionsCommand(), new DeployTemporalCollectionsLSQTCommand(),
			new DeployOtherDatabasesCommand());

		try {
			appDeployer.deploy(appConfig);

			verifyTemporalDocsExist(appConfig.getContentDatabaseName());
			verifyTemporalDocsExist("other-" + appConfig.getContentDatabaseName());
		} finally {
			undeploySampleApp();
		}
	}

	private void verifyTemporalDocsExist(String databaseName) {
		ResourcesFragment axes = new TemporalAxesManager(manageClient, databaseName).getAsXml();
		assertEquals(2, axes.getResourceCount());

		ResourcesFragment collections = new TemporalCollectionManager(manageClient, databaseName).getAsXml();
		assertEquals(1, collections.getResourceCount());
	}
}
