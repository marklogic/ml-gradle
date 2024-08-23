/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.AmpManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManageAmpsTest extends AbstractManageResourceTest {

	@Test
	void updateAndDeleteJavascriptAmp() throws Exception {
		Amp amp = new Amp(new API(manageClient), "aaa-function");
		amp.setDocumentUri("/some/module.sjs");
		amp.setModulesDatabase("Modules");
		amp.addRole("rest-reader");

		AmpManager mgr = new AmpManager(manageClient);
		assertFalse(mgr.ampExists(amp.getJson()));

		mgr.save(amp.getJson());
		assertTrue(mgr.ampExists(amp.getJson()));

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode ampJson = mapper.readTree(mgr.getAsJson("aaa-function", "document-uri", "/some/module.sjs", "modules-database", "Modules"));
			assertEquals(1, ampJson.get("role").size());
			assertEquals("rest-reader", ampJson.get("role").get(0).asText());

			amp.getRole().add("rest-writer");
			mgr.save(amp.getJson());

			ampJson = mapper.readTree(mgr.getAsJson("aaa-function", "document-uri", "/some/module.sjs", "modules-database", "Modules"));
			assertEquals(2, ampJson.get("role").size());

			ampJson.get("role").iterator().forEachRemaining(node -> {
				String role = node.asText();
				assertTrue("rest-reader".equals(role) || "rest-writer".equals(role));
			});
		} finally {
			mgr.delete(amp.getJson());
			assertFalse(mgr.ampExists(amp.getJson()), "The amp should have been deleted, even though it does not have a " +
				"namespace value. Per the Manage API docs, namespace is a required parameter and thus it must still be " +
				"defined as 'namespace='.");
		}
	}

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

		AmpManager mgr = new AmpManager(super.manageClient);

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
