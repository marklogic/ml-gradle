package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileProcessor;
import com.marklogic.client.modulesloader.ModulesManager;

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
			if (!modulesManager.hasFileBeenModifiedSinceLastInstalled(file)) {
				return null;
			}
			modulesManager.saveLastInstalledTimestamp(file, new Date());
		}
		return documentFile;
	}
}
