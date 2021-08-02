package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.AmpManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ManageAmpsTest extends AbstractManageResourceTest {

	@Test
	public void ampLoadedBeforeModules() {
		appConfig.setRestPort(8004);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/real-amp")));

		initializeAppDeployer(new DeployUsersCommand(), new DeployRestApiServersCommand(true),
			new DeployAmpsCommand(), new LoadModulesCommand());
		appConfig.setModuleTimestampsPath(null);

		try {
			deploySampleApp();

			// Create a client for a user that does not have the status privilege, which is required
			// by the get-host-status function
			DatabaseClient client = DatabaseClientFactory.newClient(super.manageConfig.getHost(), appConfig.getRestPort(),
				new DatabaseClientFactory.DigestAuthContext("sample-app-jane", "password"));

			String output = client.newServerEval().xquery(
				"import module namespace sample = 'urn:sampleapp' at '/ext/sample-lib.xqy'; " +
				"sample:get-host-status()").evalAs(String.class);

			assertNotNull(output, "The amp is loaded before the module, but it should still apply and allow the user " +
				"to invoke the function that requires the status-builtins privilege");
		} finally {
			undeploySampleApp();
		}
	}

	/**
	 * This test verifies that AmpManager can correctly handle two amps that have the same local-name, document-uri,
	 * and namespace, but with a different modules-database.
	 *
	 * Note that the module doesn't actually have to exist in order to create an amp.
	 */
	@Test
	public void twoAmpsWithDifferentModulesDatabase() {
		String amp1 = "{\n" +
			"  \"namespace\": \"http://example.com/uri\",\n" +
			"  \"local-name\": \"ml-app-deployer-test-amp\",\n" +
			"  \"document-uri\": \"/module/path/name\",\n" +
			"  \"modules-database\": \"Documents\",\n" +
			"  \"role\": [\"rest-writer\"]\n" +
			"}";

		String amp2 = "{\n" +
			"  \"namespace\": \"http://example.com/uri\",\n" +
			"  \"local-name\": \"ml-app-deployer-test-amp\",\n" +
			"  \"document-uri\": \"/module/path/name\",\n" +
			"  \"modules-database\": \"Modules\",\n" +
			"  \"role\": [\"rest-writer\"]\n" +
			"}";

		ManageClient client = new ManageClient();
		AmpManager mgr = new AmpManager(client);

		try {
			// Create and verify
			mgr.save(amp1);
			mgr.save(amp2);
			assertTrue(mgr.ampExists(amp1));
			assertTrue(mgr.ampExists(amp2));

			// Update and verify
			mgr.save(amp1);
			mgr.save(amp2);
			assertTrue(mgr.ampExists(amp1));
			assertTrue(mgr.ampExists(amp2));
		} finally {
			// Delete and verify
			mgr.delete(amp1);
			mgr.delete(amp2);
			assertFalse(mgr.ampExists(amp1));
			assertFalse(mgr.ampExists(amp2));
		}
	}

	@Override
	protected ResourceManager newResourceManager() {
		return new AmpManager(manageClient);
	}

	@Override
	protected Command newCommand() {
		return new DeployAmpsCommand();
	}

	/**
	 * The second amp doesn't have a modules database specified, so we can verify the amp can still be
	 * created/deleted when it refers to a filesystem module.
	 *
	 * @return
	 */
	@Override
	protected String[] getResourceNames() {
		return new String[]{"ml-app-deployer-test-1", "ml-app-deployer-test-2"};
	}

}
