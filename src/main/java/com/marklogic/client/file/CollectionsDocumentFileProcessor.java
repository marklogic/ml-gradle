package com.marklogic.client.file;

import com.marklogic.client.helper.LoggingObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Looks for a special file in each directory - defaults to collections.properties - that contains properties where the
 * key is the name of a file in the directory, and the value is a comma-delimited list of collections to load the file
 * into (which means you can't use a comma in any collection name). This will by default NOT load the configured
 * properties file, as that's expected to just be configuration data and not something that's intended to be loaded
 * into MarkLogic.
 */
public class CollectionsDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor {

	private String collectionsFilename = "collections.properties";

	// Used to avoid checking for and loading the properties for every file in a directory
	private Map<File, Properties> propertiesCache = new HashMap<>();

	/**
	 * @param documentFile
	 * @return
	 */
	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		File file = documentFile.getFile();
		String name = file.getName();
		if (collectionsFilename.equals(name)) {
			return null;
		}

		File collectionsFile = new File(file.getParentFile(), collectionsFilename);
		if (collectionsFile.exists()) {
			try {
				Properties props = loadProperties(collectionsFile);
				if (props.containsKey(name)) {
					String value = props.getProperty(name);
					documentFile.getDocumentMetadata().withCollections(value.split(","));
				}
			} catch (IOException e) {
				logger.warn("Unable to load properties from collections file: " + collectionsFile.getAbsolutePath(), e);
			}
		}

		return documentFile;
	}

	protected Properties loadProperties(File collectionsFile) throws IOException {
		Properties props = null;
		if (propertiesCache.containsKey(collectionsFile)) {
			props = propertiesCache.get(collectionsFile);
		}
		if (props != null) {
			return props;
		}
		props = new Properties();
		FileReader reader = null;
		try {
			reader = new FileReader(collectionsFile);
			props.load(reader);
			propertiesCache.put(collectionsFile, props);
			return props;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public Map<File, Properties> getPropertiesCache() {
		return propertiesCache;
	}

	public void setCollectionsFilename(String collectionsFilename) {
		this.collectionsFilename = collectionsFilename;
	}
}
