/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
