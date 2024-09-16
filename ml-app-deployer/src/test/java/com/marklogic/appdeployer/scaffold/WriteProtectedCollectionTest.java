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
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand;
import com.marklogic.mgmt.api.security.Permission;
import com.marklogic.mgmt.api.security.ProtectedCollection;
import com.marklogic.mgmt.template.security.ProtectedCollectionTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WriteProtectedCollectionTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployProtectedCollectionsCommand());
		buildResourceAndDeploy(new ProtectedCollectionTemplateBuilder());

		ProtectedCollection pc = api.protectedCollection("CHANGEME-collection-to-protect");
		assertNotNull(pc);

		if (false) {
			// Not running this due to https://bugtrack.marklogic.com/55358, which prevents permissions from
			// being returned from the Manage API on protected collections
			assertEquals(1, pc.getPermission().size());
			Permission perm = pc.getPermission().get(0);
			assertEquals("rest-reader", perm.getRoleName());
			assertEquals("update", perm.getCapability());
		}
	}
}
