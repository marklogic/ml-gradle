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
