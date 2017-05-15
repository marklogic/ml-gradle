package com.marklogic.client.file;

import java.util.Properties;

/**
 * Looks for a special file in each directory - defaults to queryCollections.properties - that contains properties where the
 * key is the name of a file in the directory, and the value is a comma-delimited list of queryCollections to load the file
 * into (which means you can't use a comma in any collection name).
 */
public class CollectionsDocumentFileProcessor extends PropertiesDrivenDocumentFileProcessor {

	public CollectionsDocumentFileProcessor() {
		this("queryCollections.properties");
	}

	public CollectionsDocumentFileProcessor(String propertiesFilename) {
		super(propertiesFilename);
	}

	@Override
	protected void processProperties(DocumentFile documentFile, Properties properties) {
		String name = documentFile.getFile().getName();
		if (properties.containsKey(name)) {
			String value = properties.getProperty(name);
			documentFile.getDocumentMetadata().withCollections(value.split(","));
		}
	}
}
