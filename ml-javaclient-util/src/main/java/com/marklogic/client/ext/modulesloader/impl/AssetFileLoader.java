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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.batch.BatchWriter;
import com.marklogic.client.ext.file.GenericFileLoader;
import com.marklogic.client.ext.modulesloader.ModulesManager;

/**
 * File loaded for "assets", as defined by the REST API - basically, any server module. Be sure to use a DatabaseClient
 * that points to your modules database.
 */
public class AssetFileLoader extends GenericFileLoader {

	public final static String DEFAULT_PERMISSIONS = "rest-admin,read,rest-admin,update,rest-extension-user,execute";

	public AssetFileLoader(DatabaseClient modulesDatabaseClient) {
		this(modulesDatabaseClient, null);
	}

	public AssetFileLoader(DatabaseClient modulesDatabaseClient, ModulesManager modulesManager) {
		super(modulesDatabaseClient);
		initializeAssetFileLoader(modulesManager);
	}

	public AssetFileLoader(BatchWriter batchWriter) {
		this(batchWriter, null);
	}

	public AssetFileLoader(BatchWriter batchWriter, ModulesManager modulesManager) {
		super(batchWriter);
		initializeAssetFileLoader(modulesManager);
	}

	protected void initializeAssetFileLoader(ModulesManager modulesManager) {
		addFileFilter(new DefaultFileFilter());
		addDocumentFileProcessor(new ExtDocumentFileProcessor());
		if (modulesManager != null) {
			addDocumentFileProcessor(new ModulesManagerDocumentFileProcessor(modulesManager));
		}
		setPermissions(DEFAULT_PERMISSIONS);
	}
}
