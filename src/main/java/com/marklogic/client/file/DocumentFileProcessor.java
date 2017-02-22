package com.marklogic.client.file;

/**
 * Callback-style interface for processing - i.e. do whatever you want with - a DocumentFile instance before it's
 * written to MarkLogic.
 */
public interface DocumentFileProcessor {

	boolean supportsDocumentFile(DocumentFile documentFile);

	void processDocumentFile(DocumentFile documentFile);
}
