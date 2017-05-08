package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.forests.DeployCustomForestsCommand;
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.mgmt.security.PrivilegeManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ExportPrivilegesTest extends AbstractExportTest {

	private String privilegeName = "sample-app-execute-1";

	@Before
	public void setup() {
		appConfig.getConfigDir().setBaseDir(exportDir);
		initializeAppDeployer(new DeployPrivilegesCommand());
	}

	@After
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void executePrivilege() {
		String payload = buildPayload(privilegeName, "execute");

		PrivilegeManager mgr = new PrivilegeManager(manageClient);
		mgr.save(payload);
		assertTrue(mgr.exists(privilegeName));

		ExportedResources resources = new Exporter(manageClient).privilegesExecute(privilegeName).export(exportDir);
		assertEquals(1, resources.getFiles().size());
		assertEquals(1, resources.getMessages().size());

		mgr.delete(payload);
		assertFalse(mgr.exists(privilegeName));

		deploySampleApp();
		assertTrue(mgr.exists(privilegeName));
	}

	@Test
	public void uriPrivilege() {
		String payload = buildPayload(privilegeName, "uri");

		PrivilegeManager mgr = new PrivilegeManager(manageClient);
		mgr.save(payload);
		assertTrue(mgr.exists(privilegeName));

		ExportedResources resources = new Exporter(manageClient).privilegesUri(privilegeName).export(exportDir);
		assertEquals(1, resources.getFiles().size());
		assertEquals(1, resources.getMessages().size());

		mgr.delete(payload);
		assertFalse(mgr.exists(privilegeName));

		deploySampleApp();
		assertTrue(mgr.exists(privilegeName));
	}

	private String buildPayload(String privilegeName, String kind) {
		String payload = "{\n" +
			"  \"privilege-name\":\"%s\", \n" +
			"  \"action\": \"urn:sample-app:privilege:1\",\n" +
			"  \"kind\":\"%s\"\n" +
			"}";
		return format(payload, privilegeName, kind);
	}
}
