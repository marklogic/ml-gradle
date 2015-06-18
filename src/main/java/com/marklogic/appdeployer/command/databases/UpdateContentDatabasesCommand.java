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
        AppConfig appConfig = context.getAppConfig();

        List<File> files = appConfig.getConfigDir().getContentDatabaseFiles();
        logger.info("Merging JSON files at locations: " + files);
        JsonNode node = JsonNodeUtil.mergeJsonFiles(files);

        if (node == null) {
            logger.info(format("No content database files found in directory %s, so no updating content databases",
                    appConfig.getConfigDir().getDatabasesDir().getAbsolutePath()));
            return;
        }

        String payload = node.toString();
        String json = tokenReplacer.replaceTokens(payload, appConfig, false);
        if (logger.isDebugEnabled()) {
            logger.debug("Content database JSON: " + payload);
        }

        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        dbMgr.save(json);

        if (appConfig.isTestPortSet()) {
            json = tokenReplacer.replaceTokens(payload, appConfig, true);
            dbMgr.save(json);
        }
    }
}
