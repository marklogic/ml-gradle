package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class DeployRolesWithDependenciesTest extends AbstractAppDeployerTest {

	@Before
	public void setup() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-dependencies"));
	}

	@Test
	public void testSorting() {
		DeployRolesCommand command = new DeployRolesCommand();
		File[] files = command.listFilesInDirectory(appConfig.getConfigDir().getRolesDir(),
			new CommandContext(appConfig, manageClient, null));

		assertEquals("role3.json", files[0].getName());
		assertEquals("role2.xml", files[1].getName());
		assertEquals("role1.json", files[2].getName());
		assertEquals("role4.xml", files[3].getName());
		assertEquals("role5.json", files[4].getName());
		assertEquals("role0.json", files[5].getName());
	}

	@Test
	public void test() {
		initializeAppDeployer(new DeployRolesCommand());

		try {
			appDeployer.deploy(appConfig);

			RoleManager mgr = new RoleManager(manageClient);
			for (int i = 0; i <= 5; i++) {
				assertTrue(mgr.exists("sample-app-role" + i));
			}
		} finally {
			undeploySampleApp();
		}
	}
}
