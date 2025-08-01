/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.databases;

import java.io.File;

/**
 * Abstracts how a DeployDatabaseCommand is instantiated so that Data Hub Framework can provide in its
 * own implementation with DHF-specific functionality in it.
 */
public interface DeployDatabaseCommandFactory {

	/**
	 * @param databaseFile can be null
	 * @return
	 */
	DeployDatabaseCommand newDeployDatabaseCommand(File databaseFile);

}
