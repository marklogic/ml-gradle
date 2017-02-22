package com.marklogic.client.schemasloader.impl;

import com.marklogic.client.file.DocumentFile;
import com.marklogic.client.file.DocumentFileProcessor;
import com.marklogic.client.io.Format;

public class TdeDocumentFileProcessor implements DocumentFileProcessor {

	@Override
	public boolean supportsDocumentFile(DocumentFile documentFile) {
		String extension = documentFile.getFileExtension();
		return "tdej".equals(extension) || "tdex".equals(extension);
	}

	@Override
	public void processDocumentFile(DocumentFile documentFile) {
		documentFile.getDocumentMetadata().withCollections("http://marklogic.com/xdmp/tde");

		String extension = documentFile.getFileExtension();
		if ("tdej".equals(extension)) {
			documentFile.setFormat(Format.JSON);
		} else if ("tdex".equals(extension)) {
			documentFile.setFormat(Format.XML);
		}
	}
}
