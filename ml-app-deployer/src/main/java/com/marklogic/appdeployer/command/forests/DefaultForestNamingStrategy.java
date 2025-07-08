/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;

public class DefaultForestNamingStrategy implements ForestNamingStrategy {

	@Override
	public String getForestName(String databaseName, int forestNumber, AppConfig appConfig) {
		return databaseName + "-" + forestNumber;
	}

	@Override
	public String getReplicaName(String databaseName, String forestName, int forestReplicaNumber, AppConfig appConfig) {
		return forestName + "-replica-" + forestReplicaNumber;
	}
}
