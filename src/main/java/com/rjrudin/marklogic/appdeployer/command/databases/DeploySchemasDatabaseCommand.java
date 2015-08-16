package com.rjrudin.marklogic.appdeployer.command.databases;

import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;

public class DeploySchemasDatabaseCommand extends DeployDatabaseCommand {

    public DeploySchemasDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_SCHEMAS_DATABASE);
        setUndoSortOrder(SortOrderConstants.DELETE_SCHEMAS_DATABASE);
        setDatabaseFilename("schemas-database.json");
    }
}
