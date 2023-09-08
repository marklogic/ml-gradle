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

import java.util.Properties;

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

	@Override
	protected void processProperties(DocumentFile documentFile, Properties properties) {
		String name = documentFile.getFile().getName();
		if (properties.containsKey(name)) {
			String value = getPropertyValue(properties, name);
			documentFile.getDocumentMetadata().withCollections(value.split(delimiter));
		}

		if (properties.containsKey(WILDCARD_KEY)) {
			String value = getPropertyValue(properties, WILDCARD_KEY);
			documentFile.getDocumentMetadata().withCollections(value.split(delimiter));
		}
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
