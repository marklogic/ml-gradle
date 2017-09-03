package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.Test;

import java.util.regex.Pattern;

public class IgnoreRoleTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setResourceFilenamesToIgnore("sample-app-role2.xml");
		initializeAppDeployer(new DeployRolesCommand());
		appDeployer.deploy(appConfig);

		try {
			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-role1"));
			assertFalse("Role should not have been created because its resource file was ignored", mgr.exists("sample-app-role2"));
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void excludeRoles() {
		appConfig.setResourceFilenamesExcludePattern(Pattern.compile(".*role1.*"));
		initializeAppDeployer(new DeployRolesCommand());
		appDeployer.deploy(appConfig);

		try {
			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-role2"));
			assertFalse("Role should not have been created because its filename was excluded via regex", mgr.exists("sample-app-role1"));
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void includeRoles() {
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*role1.*"));
		initializeAppDeployer(new DeployRolesCommand());
		appDeployer.deploy(appConfig);

		try {
			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-role1"));
			assertFalse("Role should not have been created because its filename was not included via regex", mgr.exists("sample-app-role2"));
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void includeAndExcludeRoles() {
		appConfig.setResourceFilenamesExcludePattern(Pattern.compile(".*role2.*"));
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*role1.*"));
		initializeAppDeployer(new DeployRolesCommand());
		try {
			appDeployer.deploy(appConfig);
			fail("Deployment should have failed because exclude and include patterns can't both be set");
		} catch (Exception ex) {
			assertEquals("Both excludePattern and includePattern cannot be specified", ex.getMessage());
		} finally {
			undeploySampleApp();
		}
	}
}
