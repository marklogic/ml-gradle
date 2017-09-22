package com.marklogic.client.ext.file;

import com.marklogic.client.ext.file.DefaultDocumentFileReader;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileReader;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DocumentFinderTest extends Assert {

	private DocumentFileReader sut = new DefaultDocumentFileReader();

	@Test
	public void noFileFilter() {
		String path = Paths.get("src", "test", "resources", "schemas").toString();
		List<DocumentFile> list = sut.readDocumentFiles(path);
		assertEquals(5, list.size());

		List<String> uris = new ArrayList<>();
		for (DocumentFile file : list) {
			uris.add(file.getUri());
		}
		assertTrue(uris.contains("/child/child.tdej"));
		assertTrue(uris.contains("/child/grandchild/grandchild.tdex"));
		assertTrue(uris.contains("/parent.tdex"));
		assertTrue(uris.contains("/tde/ruleset.txt"));
		assertTrue(uris.contains("/not-tde/ruleset.txt"));
	}
}
