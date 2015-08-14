package com.rjrudin.marklogic.appdeployer.command.databases;

import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;

public class CreateSchemasDatabaseCommand extends CreateDatabaseCommand {

    public CreateSchemasDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_SCHEMAS_DATABASE);
        setUndoSortOrder(SortOrderConstants.DELETE_SCHEMAS_DATABASE);
        setDatabaseFilename("schemas-database.json");
    }
}
