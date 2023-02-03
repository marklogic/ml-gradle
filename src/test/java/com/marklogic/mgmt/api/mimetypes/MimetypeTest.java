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
