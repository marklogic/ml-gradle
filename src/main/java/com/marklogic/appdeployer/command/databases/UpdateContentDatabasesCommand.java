package com.marklogic.appdeployer.command.databases;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.util.JsonNodeUtil;

public class UpdateContentDatabasesCommand extends AbstractCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.UPDATE_CONTENT_DATABASES_ORDER;
    }

    @Override
    public void execute(CommandContext context) {
        List<File> files = context.getAppConfig().getConfigDir().getContentDatabaseFiles();
        JsonNode node = JsonNodeUtil.mergeJsonFiles(files);

        if (node == null) {
            logger.info(format("No content database files found in directory %s, so no updating content databases",
                    context.getAppConfig().getConfigDir().getDatabasesDir().getAbsolutePath()));
            return;
        }

        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        String payload = node.toString();
        logger.info(payload);

        AppConfig appConfig = context.getAppConfig();

        String json = tokenReplacer.replaceTokens(payload, appConfig, false);
        dbMgr.updateDatabase(appConfig.getContentDatabaseName(), json);

        if (appConfig.isTestPortSet()) {
            json = tokenReplacer.replaceTokens(payload, appConfig, true);
            dbMgr.updateDatabase(appConfig.getTestContentDatabaseName(), json);
        }
    }
}
