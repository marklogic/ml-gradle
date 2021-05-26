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
