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
package com.marklogic.client.ext.file;

import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.io.Format;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SetAdditionalBinaryExtensionsTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		client = newClient(CONTENT_DATABASE);
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();

		GenericFileLoader loader = new GenericFileLoader(client);
		loader.setAdditionalBinaryExtensions("test1", "test2");
		List<DocumentFile> files = loader.loadFiles(Paths.get("src", "test", "resources", "binary-test").toString());
		for (DocumentFile file : files) {
			String name = file.getFile().getName();
			if ("file.test1".equals(name) || "file.test2".equals(name)) {
				assertEquals(Format.BINARY, file.getFormat());
			} else {
				assertEquals(Format.TEXT, file.getFormat(), "Unrecognized extensions should default to text");
			}
		}
	}
}
