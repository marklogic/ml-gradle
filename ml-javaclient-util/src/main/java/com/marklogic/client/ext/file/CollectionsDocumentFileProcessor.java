/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

/**
 * Adds the given collections to each DocumentFile that it processes.
 */
public class CollectionsDocumentFileProcessor implements DocumentFileProcessor {

	private String[] collections;

	public CollectionsDocumentFileProcessor(String... collections) {
		this.collections = collections;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		if (collections != null && documentFile.getDocumentMetadata() != null) {
			documentFile.getDocumentMetadata().withCollections(collections);
		}
		return documentFile;
	}
}
