/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.file;

import com.marklogic.client.ext.util.DefaultDocumentPermissionsParser;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.ext.util.DocumentPermissionsParser;

/**
 * DocumentFileProcessor that uses a DocumentPermissionsParser to parse a string of permissions (typically, a delimited
 * string of roles and capabilities) and adds them to each DocumentFile.
 */
public class PermissionsDocumentFileProcessor implements DocumentFileProcessor {

	private String permissions;
	private DocumentPermissionsParser documentPermissionsParser;

	public PermissionsDocumentFileProcessor(String permissions) {
		this(permissions, new DefaultDocumentPermissionsParser());
	}

	public PermissionsDocumentFileProcessor(String permissions, DocumentPermissionsParser documentPermissionsParser) {
		this.permissions = permissions;
		this.documentPermissionsParser = documentPermissionsParser;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		if (permissions != null && documentPermissionsParser != null) {
			DocumentMetadataHandle metadata = documentFile.getDocumentMetadata();
			if (metadata != null) {
				documentPermissionsParser.parsePermissions(permissions, metadata.getPermissions());
			}
		}
		return documentFile;
	}
}
