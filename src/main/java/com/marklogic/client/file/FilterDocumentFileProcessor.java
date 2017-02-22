package com.marklogic.client.file;

import java.io.FileFilter;

/**
 * Simple filter implementation for returning null if the DocumentFile doesn't match the given FileFilter.
 */
public class FilterDocumentFileProcessor implements DocumentFileProcessor {

	private FileFilter fileFilter;

	public FilterDocumentFileProcessor(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		return fileFilter.accept(documentFile.getFile()) ? documentFile : null;
	}
}
