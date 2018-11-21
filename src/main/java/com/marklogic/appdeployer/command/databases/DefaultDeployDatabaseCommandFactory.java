package com.marklogic.appdeployer.command.databases;

import java.io.File;

public class DefaultDeployDatabaseCommandFactory implements DeployDatabaseCommandFactory {

	@Override
	public DeployDatabaseCommand newDeployDatabaseCommand(File databaseFile) {
		return new DeployDatabaseCommand(databaseFile);
	}

}
