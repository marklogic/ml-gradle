package com.marklogic.appdeployer.command.mimetypes;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.mimetypes.MimetypeManager;
import org.junit.After;
import org.junit.Test;

public class DontRestartWhenMimetypePropertiesArentUpdatedTest extends AbstractAppDeployerTest {

	private MimetypeManager mimetypeManager;

	@After
	public void teardown() {
		mimetypeManager.deleteByIdField("application/ditamap+xml");
		assertFalse(mimetypeManager.exists("application/ditamap+xml"));
	}

	@Test
	public void test() {
		mimetypeManager = new MimetypeManager(manageClient);

		initializeAppDeployer(new DeployMimetypesCommand());

		deploySampleApp();

		// Deploy again, though we don't have a good way of asserting that ML didn't restart; can check the logs
		appConfig.setUpdateMimetypeWhenPropertiesAreEqual(true);
		deploySampleApp();

		// But we can verify that MimetypeManager doesn't cause an update
		String payload = readTestResource("sample-app/src/main/ml-config/mimetypes/ditamap.json");

		SaveReceipt receipt = mimetypeManager.save(payload);
		assertNull("The response should be null since no call was made to the Manage API since the mimetype " +
			"properties weren't updated", receipt.getResponse());
		assertFalse(receipt.hasLocationHeader());

		// Make sure XML works too
		payload = "<mimetype-properties xmlns='http://marklogic.com/manage'>\n" +
			"  <name>application/ditamap+xml</name>\n" +
			"  <extensions>\n" +
			"    <extension>ditamap</extension>\n" +
			"  </extensions>\n" +
			"  <format>xml</format>\n" +
			"</mimetype-properties>";
		receipt = mimetypeManager.save(payload);
		assertNull("The response should be null since no call was made to the Manage API since the mimetype " +
			"properties weren't updated", receipt.getResponse());
		assertFalse(receipt.hasLocationHeader());

		// And make sure we do get a restart if the properties change
		payload = payload.replace("<format>xml</format>", "<format>text</format>");
		receipt = mimetypeManager.save(payload);
		try {
			assertTrue(receipt.hasLocationHeader());
		} finally {
			adminManager.waitForRestart();
		}
	}
}
