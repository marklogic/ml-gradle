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

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentFinderTest {

	private DocumentFileReader sut = new DefaultDocumentFileReader();

	/**
	 * Note that this does find the ".do-not-load", as DefaultDocumentFileReader doesn't ignore .* files by default.
	 */
	@Test
	public void noFileFilter() {
		String path = Paths.get("src", "test", "resources", "schemas").toString();
		List<DocumentFile> list = sut.readDocumentFiles(path);
		assertEquals(6, list.size());

		List<String> uris = new ArrayList<>();
		for (DocumentFile file : list) {
			uris.add(file.getUri());
		}
		assertTrue(uris.contains("/.do-not-load"));
		assertTrue(uris.contains("/child/child.tdej"));
		assertTrue(uris.contains("/child/grandchild/grandchild.tdex"));
		assertTrue(uris.contains("/parent.tdex"));
		assertTrue(uris.contains("/tde/ruleset.txt"));
		assertTrue(uris.contains("/not-tde/ruleset.txt"));
	}
}
