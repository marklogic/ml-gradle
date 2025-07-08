/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.databases;

import java.io.File;

public class DefaultDeployDatabaseCommandFactory implements DeployDatabaseCommandFactory {

	@Override
	public DeployDatabaseCommand newDeployDatabaseCommand(File databaseFile) {
		return new DeployDatabaseCommand(databaseFile);
	}

}
