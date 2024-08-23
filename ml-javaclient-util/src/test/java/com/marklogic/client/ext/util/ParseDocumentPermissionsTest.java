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
package com.marklogic.client.ext.util;

import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseDocumentPermissionsTest {

	private DefaultDocumentPermissionsParser parser = new DefaultDocumentPermissionsParser();

	@Test
	void test() {
		String str = "rest-admin,read,rest-admin,update,rest-extension-user,execute,app-user,node-update";
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		parser.parsePermissions(str, metadata.getPermissions());

		DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
		assertEquals(3, perms.size());
		assertEquals(2, perms.get("rest-admin").size());
		assertEquals(1, perms.get("rest-extension-user").size());
		assertEquals(DocumentMetadataHandle.Capability.EXECUTE, perms.get("rest-extension-user").iterator().next());
		assertEquals(1, perms.get("app-user").size());
		assertEquals(DocumentMetadataHandle.Capability.NODE_UPDATE, perms.get("app-user").iterator().next());
	}

	@Test
	void badInput() {
		String str = "rest-admin,read,rest-admin";
		try {
			parser.parsePermissions(str, new DocumentMetadataHandle().getPermissions());
			fail("An IllegalArgumentException should be thrown because the permissions string is missing a second capability " +
				"to go with the second role");
		} catch (IllegalArgumentException ex) {
			assertTrue(ex.getMessage().startsWith("Unable to parse permissions string"));
		}
	}

	@Test
	void invalidCapability() {
		String str = "app-user,not-valid";
		IllegalArgumentException ex = assertThrows(
			IllegalArgumentException.class,
			() -> parser.parsePermissions(str, new DocumentMetadataHandle().getPermissions()));
		assertEquals("Unable to parse permissions string: app-user,not-valid; cause: No enum constant com.marklogic.client.io.DocumentMetadataHandle.Capability.NOT_VALID",
			ex.getMessage());
	}
}
