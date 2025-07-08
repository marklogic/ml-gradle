/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;

/**
 * This is just for loading REST modules, which DefaultModulesLoader loads by default in parallel. The DatabaseClient
 * is provided so that the implementation can e.g. capture information about the host and port in use.
 */
public interface LoadModulesFailureListener {

	void processFailure(Throwable throwable, DatabaseClient databaseClient);

}
