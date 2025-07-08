/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.mimetypes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MimetypeTest  {

	@Test
	public void testEquals() {
		assertEquals(
			new Mimetype("abc", "binary", "ext1", "ext2", "ext3"),
			new Mimetype("abc", "binary", "ext2", "ext3", "ext1")
		);

		assertNotEquals(
			new Mimetype("abc", "binary", "ext1", "ext2", "ext3"),
			new Mimetype("abc", "binary", "ext2", "ext3", "ext2")
		);

		assertNotEquals(
			new Mimetype("abc", "binary", "ext1"),
			new Mimetype("abcd", "binary", "ext1")
		);

		assertNotEquals(
			new Mimetype("abc", "binary", "ext1"),
			new Mimetype("abc", "binaryy", "ext1")
		);
	}
}
