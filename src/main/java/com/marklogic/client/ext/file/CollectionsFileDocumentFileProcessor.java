package com.marklogic.client.ext.file;

import java.util.Properties;

/**
 * Looks for a special file in each directory - defaults to collections.properties - that contains properties where the
 * key is the name of a file in the directory, and the value is a comma-delimited list of collections to load the file
 * into (which means you can't use a comma in any collection name).
 */
public class CollectionsFileDocumentFileProcessor extends PropertiesDrivenDocumentFileProcessor {

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
			String value = properties.getProperty(name);
			documentFile.getDocumentMetadata().withCollections(value.split(delimiter));
		}

		if (properties.containsKey(WILDCARD_KEY)) {
			String value = properties.getProperty(WILDCARD_KEY);
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
