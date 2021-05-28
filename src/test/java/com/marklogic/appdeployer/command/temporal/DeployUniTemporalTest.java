package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionManager;
import com.marklogic.rest.util.ResourcesFragment;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeployUniTemporalTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/uni-temporal-config"));

		initializeAppDeployer(
			new DeployTemporalAxesCommand(), new DeployTemporalCollectionsCommand(), new DeployTemporalCollectionsLSQTCommand(),
			new DeployOtherDatabasesCommand(1));

		try {
			appDeployer.deploy(appConfig);

			verifyTemporalDocsExist(appConfig.getContentDatabaseName());
		} finally {
			undeploySampleApp();
		}
	}

	private void verifyTemporalDocsExist(String databaseName) {
		ResourcesFragment axes = new TemporalAxesManager(manageClient, databaseName).getAsXml();
		assertEquals(1, axes.getResourceCount());

		ResourcesFragment collections = new TemporalCollectionManager(manageClient, databaseName).getAsXml();
		assertEquals(1, collections.getResourceCount());
	}
}
