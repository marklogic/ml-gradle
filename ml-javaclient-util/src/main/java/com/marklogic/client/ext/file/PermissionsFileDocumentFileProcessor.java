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
