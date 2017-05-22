package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileProcessor;

import java.nio.file.Path;

/**
 * Appends "/ext" to assets loaded from the REST API-specific "/ext" directory.
 */
public class ExtDocumentFileProcessor implements DocumentFileProcessor {

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		Path rootPath = documentFile.getRootPath();
		if (rootPath != null) {
			String name = rootPath.toFile().getName();
			if ("ext".equalsIgnoreCase(name)) {
				documentFile.setUri("/ext" + documentFile.getUri());
			}
		}
		return documentFile;
	}

}
