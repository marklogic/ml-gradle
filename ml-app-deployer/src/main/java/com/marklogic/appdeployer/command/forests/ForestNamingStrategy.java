/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;

public interface ForestNamingStrategy {

	/**
	 * @param databaseName
	 * @param forestNumber Keeps track of the total number of primary forests being created for the given database
	 * @param appConfig
	 * @return
	 */
	String getForestName(String databaseName, int forestNumber, AppConfig appConfig);

	/**
	 * @param databaseName
	 * @param forestName
	 * @param forestReplicaNumber Keeps track of the total number of replica forests being created for the given
	 *                             forest
	 * @param appConfig
	 * @return
	 */
	String getReplicaName(String databaseName, String forestName, int forestReplicaNumber, AppConfig appConfig);

}
