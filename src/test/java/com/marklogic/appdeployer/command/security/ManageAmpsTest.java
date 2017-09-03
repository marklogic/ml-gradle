package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.AmpManager;
import org.junit.Test;

public class ManageAmpsTest extends AbstractManageResourceTest {

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
