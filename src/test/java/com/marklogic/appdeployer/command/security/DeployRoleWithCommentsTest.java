package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;

public class DeployRoleWithCommentsTest extends AbstractAppDeployerTest {

	/**
	 * This verifies that if we configure a command to not clean JSON payload files, and a JSON payload has comments
	 * in it, then the deploy will fail.
	 */
	@Test
	public void test() {
		// We only want to process sample-app-role1.json
		appConfig.setResourceFilenamesToIgnore("sample-app-role2.xml");

		// As of 3.14.0, gotta turn off resource merging for this to be tested.
		appConfig.setMergeResources(false);

		initializeAppDeployer(new DeployRolesCommand());
		try {
			manageConfig.setCleanJsonPayloads(false);
			appDeployer.deploy(appConfig);
			undeploySampleApp();
			fail("The role deployment should have failed because sample-app-role1.json has comments in it");
		} catch (HttpClientErrorException ex) {
			assertEquals("Bad Request", ex.getStatusText());
		} finally {
			manageConfig.setCleanJsonPayloads(true);
		}
	}
}
