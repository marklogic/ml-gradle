package com.marklogic.client.ext.tokenreplacer;

import com.marklogic.client.ext.helper.LoggingObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Simple implementation of PropertiesSource that reads properties from a file.
 */
public class FilePropertiesSource extends LoggingObject implements PropertiesSource {

	private Properties props;
	private File file;

	public FilePropertiesSource(File file) {
		this.file = file;
	}

	@Override
	public Properties getProperties() {
		if (props == null) {
			props = loadPropertiesFromFile(file);
		}
		return props;
	}

	/**
	 * @param file
	 * @return a Properties instance based on properties from the given File.
	 */
	protected Properties loadPropertiesFromFile(File file) {
		Properties properties = new Properties();
		if (file.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				if (logger.isDebugEnabled()) {
					logger.debug("Loading properties from: " + file.getAbsolutePath());
				}
				properties.load(reader);
			} catch (IOException ex) {
				logger.warn(
					"Unable to load properties from file " + file.getAbsolutePath() + "; cause: " + ex.getMessage(),
					ex);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ie) {
						// Ignore
					}
				}
			}
		}
		return properties;
	}
}
