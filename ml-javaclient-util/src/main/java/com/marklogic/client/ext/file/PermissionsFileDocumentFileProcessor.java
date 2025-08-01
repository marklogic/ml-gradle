/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

/**
 * Looks for a special file in each directory - defaults to permissions.properties - that contains properties where the
 * key is the name of a file in the directory, and the value is a comma-delimited list of role,capability,role,capability,etc.
 */
public class PermissionsFileDocumentFileProcessor extends CascadingPropertiesDrivenDocumentFileProcessor {

	public PermissionsFileDocumentFileProcessor() {
		this("permissions.properties");
	}

	public PermissionsFileDocumentFileProcessor(String propertiesFilename) {
		super(propertiesFilename);
	}

	protected void applyPropertyMatch(DocumentFile documentFile, String pattern, String value) {
		documentFile.getDocumentMetadata().getPermissions().addFromDelimitedString(value);
	}
}
