/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

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
