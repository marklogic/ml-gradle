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
package com.marklogic.appdeployer.command;

import com.marklogic.client.ext.helper.LoggingObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Simple filter implementation that returns true for .json and .xml files.
 * <p>
 * As of 3.10.0, now implements IncrementalFilenameFilter to include support for incremental deployments - i.e. only
 * accepting a file if it is new or hasn't been modified since the last deployment.
 */
public class ResourceFilenameFilter extends LoggingObject implements IncrementalFilenameFilter {

	private Set<String> filenamesToIgnore;
	private Pattern excludePattern;
	private Pattern includePattern;
	private ResourceFileManager resourceFileManager;

	private boolean incrementalMode = false;

	private Set<String> supportedFilenameExtensions = new HashSet<>();

	public ResourceFilenameFilter() {
		this(new ResourceFileManagerImpl());
	}

	public ResourceFilenameFilter(ResourceFileManager resourceFileManager) {
		this.resourceFileManager = resourceFileManager;
		this.resourceFileManager.initialize();
		supportedFilenameExtensions.add(".xml");
		supportedFilenameExtensions.add(".json");
	}

	public ResourceFilenameFilter(String... filenamesToIgnore) {
		this();
		this.filenamesToIgnore = new HashSet<>();
		this.filenamesToIgnore.addAll(Arrays.asList(filenamesToIgnore));
	}

	public ResourceFilenameFilter(Set<String> filenamesToIgnore) {
		this();
		this.filenamesToIgnore = filenamesToIgnore;
	}

	@Override
	public boolean accept(File dir, String filename) {
		if (excludePattern != null && includePattern != null) {
			throw new IllegalStateException("Both excludePattern and includePattern cannot be specified");
		}

		if (excludePattern != null) {
			if (excludePattern.matcher(filename).matches()) {
				if (logger.isInfoEnabled()) {
					logger.info(format("Filename %s matches excludePattern, so ignoring", filename));
				}
				return false;
			}
		}

		if (includePattern != null) {
			if (!includePattern.matcher(filename).matches()) {
				if (logger.isInfoEnabled()) {
					logger.info(format("Filename %s doesn't match includePattern, so ignoring", filename));
				}
				return false;
			}
		}

		if (filenamesToIgnore != null && filenamesToIgnore.contains(filename)) {
			if (logger.isInfoEnabled()) {
				logger.info("Ignoring filename: " + filename);
			}
			return false;
		}

		if (filenameHasSupportedExtension(filename)) {
			if (incrementalMode) {
				return acceptFileBasedOnIncrementalCheck(dir, filename);
			}
			return true;
		}

		return false;
	}

	/**
	 * Determines whether the filename should be accepted based on the extension. Defaults to accepting anything with
	 * an extension of ".xml" or ".json".
	 *
	 * @param filename
	 * @return
	 */
	protected boolean filenameHasSupportedExtension(String filename) {
		if (filename == null) {
			return false;
		}
		for (String extension : supportedFilenameExtensions) {
			if (filename.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the file should be accepted based on an incremental deployment check.
	 *
	 * @param dir
	 * @param filename
	 * @return
	 */
	protected boolean acceptFileBasedOnIncrementalCheck(File dir, String filename) {
		return resourceFileManager.shouldResourceFileBeProcessed(new File(dir, filename));
	}

	public void setFilenamesToIgnore(Set<String> ignoreFilenames) {
		this.filenamesToIgnore = ignoreFilenames;
	}

	public Set<String> getFilenamesToIgnore() {
		return filenamesToIgnore;
	}

	public Pattern getExcludePattern() {
		return excludePattern;
	}

	public void setExcludePattern(Pattern excludePattern) {
		this.excludePattern = excludePattern;
	}

	public Pattern getIncludePattern() {
		return includePattern;
	}

	public void setIncludePattern(Pattern includePattern) {
		this.includePattern = includePattern;
	}

	@Override
	public void setIncrementalMode(boolean incrementalMode) {
		this.incrementalMode = incrementalMode;
	}

	public void setResourceFileManager(ResourceFileManager resourceFileManager) {
		this.resourceFileManager = resourceFileManager;
	}

	public Set<String> getSupportedFilenameExtensions() {
		return supportedFilenameExtensions;
	}

	public void setSupportedFilenameExtensions(Set<String> supportedFilenameExtensions) {
		this.supportedFilenameExtensions = supportedFilenameExtensions;
	}

	public ResourceFileManager getResourceFileManager() {
		return resourceFileManager;
	}
}
