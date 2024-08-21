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

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
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
		while (patterns.hasMoreElements()) {
			String pattern = (String) patterns.nextElement();
			PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
			if (matcher.matches(filename)) {
				String value = getPropertyValue(properties, pattern);
				this.applyPropertyMatch(documentFile, pattern, value);
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
