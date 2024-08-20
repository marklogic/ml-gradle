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
package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExportPrivilegesTest extends AbstractExportTest {

	private String privilegeName = "sample-app-execute-1";

	@BeforeEach
	public void setup() {
		appConfig.getFirstConfigDir().setBaseDir(exportDir);
		initializeAppDeployer(new DeployPrivilegesCommand());
	}

	@AfterEach
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
