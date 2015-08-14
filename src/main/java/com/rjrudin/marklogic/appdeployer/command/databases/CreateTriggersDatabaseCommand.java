package com.rjrudin.marklogic.appdeployer.command.databases;

import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;

public class CreateTriggersDatabaseCommand extends CreateDatabaseCommand {

    public CreateTriggersDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_TRIGGERS_DATABASE);
        setUndoSortOrder(SortOrderConstants.DELETE_TRIGGERS_DATABASE);
        setDatabaseFilename("triggers-database.json");

        setCreateDatabaseWithoutFile(true);
    }

    protected String buildDefaultDatabasePayload(CommandContext context) {
        return format("{\"database-name\": \"%s\"}", context.getAppConfig().getTriggersDatabaseName());
    }

}
