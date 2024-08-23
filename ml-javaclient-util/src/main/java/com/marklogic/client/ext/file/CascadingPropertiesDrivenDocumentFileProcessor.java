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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.Stack;

/**
 * Adds a stack to store Properties objects while traversing a directory tree. Implements {@code FileVisitor} so that
 * it can be informed when {@code DefaultDocumentFileReader} is entering and exiting a directory.
 *
 * To preserve backwards compatibility in subclasses, cascading is disabled by default. This will likely change in 5.0
 * to be enabled by default.
 *
 * @since 4.6.0
 */
abstract class CascadingPropertiesDrivenDocumentFileProcessor extends PropertiesDrivenDocumentFileProcessor implements FileVisitor<Path> {

	final private Stack<Properties> propertiesStack = new Stack<>();
	private boolean cascadingEnabled = false;

	protected CascadingPropertiesDrivenDocumentFileProcessor(String propertiesFilename) {
		super(propertiesFilename);
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		// If cascading is disabled, we still use a stack to keep track of whether a directory has properties or not.
		// We just never grab properties from the stack in case a directory doesn't have properties.
		if (logger.isDebugEnabled()) {
			logger.debug(format("Visiting directory: %s", dir.toFile().getAbsolutePath()));
		}
		File propertiesFile = new File(dir.toFile(), this.getPropertiesFilename());
		if (propertiesFile.exists()) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("Loading properties from file: %s", propertiesFile.getAbsolutePath()));
			}
			this.loadProperties(propertiesFile);
		} else {
			if (cascadingEnabled && !propertiesStack.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("No properties file, and cascading is enabled, so using properties from top of stack.");
				}
				this.setProperties(propertiesStack.peek());
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("No properties file, or cascading is disabled, so using empty properties.");
				}
				this.setProperties(new Properties());
			}
		}
		propertiesStack.push(this.getProperties());
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		propertiesStack.pop();
		if (!propertiesStack.isEmpty()) {
			this.setProperties(propertiesStack.peek());
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		return FileVisitResult.CONTINUE;
	}

	public boolean isCascadingEnabled() {
		return cascadingEnabled;
	}

	public void setCascadingEnabled(boolean cascadingEnabled) {
		this.cascadingEnabled = cascadingEnabled;
	}
}
