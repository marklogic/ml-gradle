package com.marklogic.client.ext.file;

import com.marklogic.client.ext.helper.LoggingObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Base class for processors that look for a special file in each directory and intend to perform some processing based
 * on the contents of that file. By default, that special file is NOT loaded into MarkLogic.
 */
public abstract class PropertiesDrivenDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor, FileFilter {

	protected final static String WILDCARD_KEY = "*";

	private String propertiesFilename;

	// Used to avoid checking for and loading the properties for every file in a directory
	private Map<File, Properties> propertiesCache = new HashMap<>();

	protected PropertiesDrivenDocumentFileProcessor(String propertiesFilename) {
		this.propertiesFilename = propertiesFilename;
	}

	@Override
	public boolean accept(File file) {
		return !file.getName().equals(propertiesFilename);
	}

	/**
	 * @param documentFile
	 * @return
	 */
	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		File file = documentFile.getFile();
		if (!accept(file)) {
			return null;
		}

		File propertiesFile = new File(file.getParentFile(), propertiesFilename);
		if (propertiesFile.exists()) {
			try {
				Properties props = loadProperties(propertiesFile);
				processProperties(documentFile, props);
			} catch (IOException e) {
				logger.warn("Unable to load properties from file: " + propertiesFile.getAbsolutePath(), e);
			}
		}

		return documentFile;
	}

	protected abstract void processProperties(DocumentFile documentFile, Properties properties);

	protected Properties loadProperties(File propertiesFile) throws IOException {
		Properties props = null;
		if (propertiesCache.containsKey(propertiesFile)) {
			props = propertiesCache.get(propertiesFile);
		}
		if (props != null) {
			return props;
		}

		props = new Properties();
		FileReader reader = null;
		try {
			reader = new FileReader(propertiesFile);
			props.load(reader);
			propertiesCache.put(propertiesFile, props);
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

	public String getPropertiesFilename() {
		return propertiesFilename;
	}
}
