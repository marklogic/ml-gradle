package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileProcessor;
import com.marklogic.client.ext.modulesloader.ModulesManager;

import java.io.File;
import java.util.Date;

public class ModulesManagerDocumentFileProcessor implements DocumentFileProcessor {

	private ModulesManager modulesManager;

	public ModulesManagerDocumentFileProcessor(ModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		File file = documentFile.getFile();
		if (file != null) {
			if (!modulesManager.hasFileBeenModifiedSinceLastLoaded(file)) {
				return null;
			}
			modulesManager.saveLastLoadedTimestamp(file, new Date());
		}
		return documentFile;
	}
}
