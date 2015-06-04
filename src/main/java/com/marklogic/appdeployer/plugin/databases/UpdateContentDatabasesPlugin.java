package com.marklogic.appdeployer.plugin.databases;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.CommandContext;
import com.marklogic.appdeployer.plugin.AbstractCommand;
import com.marklogic.appdeployer.plugin.SortOrderConstants;
import com.marklogic.rest.mgmt.databases.DatabaseManager;

public class UpdateContentDatabasesPlugin extends AbstractCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.UPDATE_CONTENT_DATABASES_ORDER;
    }

    @Override
    public void execute(CommandContext context) {
        File f = context.getAppConfig().getConfigDir().getContentDatabaseFile();
        if (f.exists()) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());

            String payload = copyFileToString(f);
            AppConfig appConfig = context.getAppConfig();

            String json = tokenReplacer.replaceTokens(payload, appConfig, false);
            dbMgr.updateDatabase(appConfig.getContentDatabaseName(), json);

            if (appConfig.isTestPortSet()) {
                json = tokenReplacer.replaceTokens(payload, appConfig, true);
                dbMgr.updateDatabase(appConfig.getTestContentDatabaseName(), json);
            }
        } else {
            logger.info(format("No content database file found at %s, so not updating the content database",
                    f.getAbsolutePath()));
        }
    }

    @Override
    public void undo(CommandContext context) {
    }

}
