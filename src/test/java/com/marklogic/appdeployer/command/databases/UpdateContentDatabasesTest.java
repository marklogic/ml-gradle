package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.rest.util.Fragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UpdateContentDatabasesTest extends AbstractAppDeployerTest {

	private DatabaseManager dbMgr;
	private String idRangeIndexPath = "//m:range-element-index[m:scalar-type = 'string' and m:namespace-uri = 'urn:sampleapp' and m:localname='id' and m:collation='http://marklogic.com/collation/']";

	@Before
	public void setup() {
		dbMgr = new DatabaseManager(manageClient);
	}

	@After
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void updateDatabase() {
		// We want both a main and a test app server in this test
		appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand());

		appDeployer.deploy(appConfig);

		Fragment db = dbMgr.getPropertiesAsXml(appConfig.getContentDatabaseName());
		assertTrue(db.elementExists(idRangeIndexPath));

		db = dbMgr.getPropertiesAsXml(appConfig.getTestContentDatabaseName());
		assertTrue(db.elementExists(idRangeIndexPath));
	}
}
