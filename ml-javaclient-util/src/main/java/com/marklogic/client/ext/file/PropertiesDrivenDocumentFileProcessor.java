/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Base class for processors that look for a special file in each directory and intend to perform some processing based
 * on the contents of that file. By default, that special file is NOT loaded into MarkLogic.
 */
public abstract class PropertiesDrivenDocumentFileProcessor extends LoggingObject
	implements DocumentFileProcessor, FileFilter, SupportsTokenReplacer {

	private final String propertiesFilename;

	private Properties properties;

	private TokenReplacer tokenReplacer;

	protected PropertiesDrivenDocumentFileProcessor(String propertiesFilename) {
		this.propertiesFilename = propertiesFilename;
	}

	@Override
	public boolean accept(File file) {
		return !file.getName().equals(propertiesFilename);
	}

	/**
	 * @param documentFile
	 * @return null if the file is not accepted, else the incoming DocumentFile
	 */
	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		File file = documentFile.getFile();
		if (!accept(file)) {
			return null;
		}
		processProperties(documentFile, properties);
		return documentFile;
	}

	/**
	 * New in 4.7.0 - each pattern in the properties object is now assumed to be a glob pattern; this retains backwards
	 * compatibility with the previous approach of only supporting "*" and exact filename matches. When a property
	 * is found to match the given file, then a subclass method is invoked to determine what to do with the value
	 * associated with the property.
	 *
	 * @param documentFile
	 * @param properties
	 */
	private void processProperties(DocumentFile documentFile, Properties properties) {
		final Path filename = documentFile.getFile().toPath().getFileName();
		Enumeration patterns = properties.propertyNames();
		FileSystem fileSystem = FileSystems.getDefault();
		try {
			while (patterns.hasMoreElements()) {
				String pattern = (String) patterns.nextElement();
				PathMatcher matcher = fileSystem.getPathMatcher("glob:" + pattern);
				if (matcher.matches(filename)) {
					String value = getPropertyValue(properties, pattern);
					if (value != null) {
						this.applyPropertyMatch(documentFile, pattern, value);
					}
				}
			}
		} finally {
			try {
				fileSystem.close();
			} catch (Exception ex) {
				// It's fine if the fileSystem doesn't support being closed, which may simply be due to not being
				// supported.
				if (logger.isDebugEnabled()) {
					logger.debug("Unable to close file system; cause: {}", ex.getMessage());
				}
			}
		}
	}

	/**
	 * Subclasses must implement this to determine how to apply the value of a matching property to the given
	 * {@code DocumentFile}.
	 *
	 * @param documentFile
	 * @param pattern
	 * @param value
	 */
	protected abstract void applyPropertyMatch(DocumentFile documentFile, String pattern, String value);

	protected final Properties loadProperties(File propertiesFile) throws IOException {
		properties = new Properties();
		try (FileReader reader = new FileReader(propertiesFile)) {
			properties.load(reader);
			return properties;
		}
	}

	private String getPropertyValue(Properties properties, String propertyName) {
		if (properties == null || propertyName == null) {
			return null;
		}
		String value = properties.getProperty(propertyName);
		return tokenReplacer != null && value != null ? tokenReplacer.replaceTokens(value) : value;
	}

	public String getPropertiesFilename() {
		return propertiesFilename;
	}

	@Override
	public void setTokenReplacer(TokenReplacer tokenReplacer) {
		this.tokenReplacer = tokenReplacer;
	}

	protected void setProperties(Properties properties) {
		this.properties = properties;
	}

	protected Properties getProperties() {
		return this.properties;
	}
}
