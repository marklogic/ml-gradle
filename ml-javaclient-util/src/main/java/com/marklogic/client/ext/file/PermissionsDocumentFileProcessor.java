/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

import com.marklogic.client.io.DocumentMetadataHandle;

/**
 * DocumentFileProcessor that uses a DocumentPermissionsParser to parse a string of permissions (typically, a delimited
 * string of roles and capabilities) and adds them to each DocumentFile.
 */
public class PermissionsDocumentFileProcessor implements DocumentFileProcessor {

	private String commaDelimitedRolesAndCapabilities;

	public PermissionsDocumentFileProcessor(String commaDelimitedRolesAndCapabilities) {
		this.commaDelimitedRolesAndCapabilities = commaDelimitedRolesAndCapabilities;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		if (this.commaDelimitedRolesAndCapabilities != null) {
			DocumentMetadataHandle metadata = documentFile.getDocumentMetadata();
			if (metadata != null) {
				metadata.getPermissions().addFromDelimitedString(this.commaDelimitedRolesAndCapabilities);
			}
		}
		return documentFile;
	}
}
