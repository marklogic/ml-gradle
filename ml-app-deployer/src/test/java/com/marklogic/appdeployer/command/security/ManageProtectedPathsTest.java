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

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.protectedpath.ProtectedPath;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedPathManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManageProtectedPathsTest extends AbstractManageResourceTest {

	@Override
	protected ResourceManager newResourceManager() {
		return new ProtectedPathManager(manageClient);
	}

	@Override
	protected Command newCommand() {
		return new DeployProtectedPathsCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[]{"/test:element"};
	}

	@Override
	protected void afterResourcesCreated() {
		API api = new API(manageClient);
		ProtectedPath path = api.protectedPath("/test:element");
		verifyProperties(path);

		// Try out XML unmarshalling too
		path = new DefaultResourceMapper(api).readResource(
			new ProtectedPathManager(manageClient).getPropertiesAsXmlString("/test:element"), ProtectedPath.class
		);
		verifyProperties(path);
	}

	private void verifyProperties(ProtectedPath path) {
		assertEquals("/test:element", path.getPathExpression());
		assertNotNull(path.getPathId());
		assertEquals(1, path.getPathNamespace().size());
		assertEquals("test", path.getPathNamespace().get(0).getPrefix());
		assertEquals("http://marklogic.com", path.getPathNamespace().get(0).getNamespaceUri());
		assertEquals(1, path.getPermission().size());
		assertEquals("view-admin", path.getPermission().get(0).getRoleName());
		assertEquals("read", path.getPermission().get(0).getCapability());
		assertEquals("the path set", path.getPathSet());
	}
}
