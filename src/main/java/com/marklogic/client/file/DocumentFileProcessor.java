package com.marklogic.client.file;

/**
 * Interface for processing - i.e. do whatever you want with - a DocumentFile instance before it's written to MarkLogic.
 */
public interface DocumentFileProcessor {

	/**
	 * @param documentFile
	 * @return the same or a new DocumentFile instance
	 */
	DocumentFile processDocumentFile(DocumentFile documentFile);

}
