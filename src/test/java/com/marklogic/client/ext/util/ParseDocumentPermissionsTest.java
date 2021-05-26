package com.marklogic.client.ext.util;

import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseDocumentPermissionsTest {

	private DefaultDocumentPermissionsParser parser = new DefaultDocumentPermissionsParser();

	@Test
	public void test() {
		String str = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		parser.parsePermissions(str, metadata.getPermissions());

		DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
		assertEquals(2, perms.size());
		assertEquals(2, perms.get("rest-admin").size());
		assertEquals(1, perms.get("rest-extension-user").size());
	}

	@Test
	public void badInput() {
		String str = "rest-admin,read,rest-admin";
		try {
			parser.parsePermissions(str, new DocumentMetadataHandle().getPermissions());
			fail("An IllegalArgumentException should be thrown because the permissions string is missing a second capability " +
				"to go with the second role");
		} catch (IllegalArgumentException ex) {
			assertTrue(ex.getMessage().startsWith("Unable to parse permissions string"));
		}
	}
}
