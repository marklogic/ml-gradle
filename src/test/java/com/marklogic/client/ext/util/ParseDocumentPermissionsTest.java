package com.marklogic.client.ext.util;

import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.Assert;
import org.junit.Test;

public class ParseDocumentPermissionsTest extends Assert {

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
}
