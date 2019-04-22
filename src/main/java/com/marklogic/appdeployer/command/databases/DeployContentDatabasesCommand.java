package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.command.CommandContext;

/**
 * As of version 3.14.0, this is now deprecated and should no longer be used, as DeployOtherDatabasesCommand handles
 * all databases.
 */
@Deprecated
public class DeployContentDatabasesCommand extends DeployOtherDatabasesCommand {

	public DeployContentDatabasesCommand() {
	}

	public DeployContentDatabasesCommand(int forestsPerHost) {
		super(forestsPerHost);
	}

	@Override
	public void execute(CommandContext context) {
		logger.warn("This command is deprecated as of release 3.14.0; DeployOtherDatabasesCommand handles all databases now.");
	}

	@Override
	public void undo(CommandContext context) {
		logger.warn("This command is deprecated as of release 3.14.0; DeployOtherDatabasesCommand handles all databases now.");
	}
}
