package com.marklogic.client.util;

import com.marklogic.client.file.DefaultDocumentFileFinder;
import com.marklogic.client.file.DocumentFile;
import com.marklogic.client.file.DocumentFileFinder;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DocumentFinderTest extends Assert {

	private DocumentFileFinder sut = new DefaultDocumentFileFinder();

	@Test
	public void noFileFilter() {
		String path = "src/test/resources/schemas";
		List<DocumentFile> list = sut.findDocumentFiles(path);
		assertEquals(3, list.size());

		List<String> uris = new ArrayList<>();
		for (DocumentFile file : list) {
			uris.add(file.getUri());
		}
		assertTrue(uris.contains("/child/child.tdej"));
		assertTrue(uris.contains("/child/grandchild/grandchild.tdex"));
		assertTrue(uris.contains("/parent.tdex"));
	}
}
