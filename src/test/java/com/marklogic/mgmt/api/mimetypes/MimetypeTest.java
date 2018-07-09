package com.marklogic.mgmt.api.mimetypes;

import org.junit.Assert;
import org.junit.Test;

public class MimetypeTest extends Assert {

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
