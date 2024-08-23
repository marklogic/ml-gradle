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
			try (FileReader reader = new FileReader(file)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Loading properties from: " + file.getAbsolutePath());
				}
				properties.load(reader);
			} catch (IOException ex) {
				logger.warn(
					"Unable to load properties from file " + file.getAbsolutePath() + "; cause: " + ex.getMessage(),
					ex);
			}
			// Ignore
		}
		return properties;
	}
}
