/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.StringHandle;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeployTemporalTest extends AbstractAppDeployerTest {

	@Test
	void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/temporal-config-with-lsqt"));

		initializeAppDeployer(new DeployTemporalAxesCommand(),
			new DeployTemporalCollectionsCommand(), new DeployTemporalCollectionsLSQTCommand(),
			new DeployOtherDatabasesCommand(1));

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

		try (DatabaseClient client = newDatabaseClient(databaseName, appConfig.getAppServicesPort())) {
			String xml = client.newXMLDocumentManager().read("temporal-collection.lsqt", new StringHandle()).get();
			Fragment doc = new Fragment(xml);
			String value = doc.getInternalDoc().getRootElement().getAttributeValue("lsqt-period");
			assertEquals("5000", value,
				"Updating LSQT properties should produce a new document in the content database containing the " +
					"property values from the LSQT properties file that was deployed.");
		}
	}
}
