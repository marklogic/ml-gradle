package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import org.junit.Test;

import java.io.File;

public class CreateAndUpdateDatabaseTest extends AbstractAppDeployerTest {

	/**
	 * TODO Update this test to perform the second deployment as a user that only has privileges to update indexes.
	 */
	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		try {
			deploySampleApp();
			deploySampleApp();
		} finally {
			undeploySampleApp();
		}
	}
}
