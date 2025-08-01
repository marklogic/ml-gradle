/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

/**
 * Looks for a special file in each directory - defaults to collections.properties - that contains properties where the
 * key is the name of a file in the directory, and the value is a comma-delimited list of collections to load the file
 * into (which means you can't use a comma in any collection name).
 */
public class CollectionsFileDocumentFileProcessor extends CascadingPropertiesDrivenDocumentFileProcessor {

	private String delimiter = ",";

	public CollectionsFileDocumentFileProcessor() {
		this("collections.properties");
	}

	public CollectionsFileDocumentFileProcessor(String propertiesFilename) {
		super(propertiesFilename);
	}

	protected void applyPropertyMatch(DocumentFile documentFile, String pattern, String value) {
		documentFile.getDocumentMetadata().withCollections(value.split(delimiter));
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
