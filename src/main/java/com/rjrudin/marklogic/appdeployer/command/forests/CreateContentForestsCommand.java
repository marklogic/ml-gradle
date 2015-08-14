package com.rjrudin.marklogic.appdeployer.command.forests;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;

public class CreateContentForestsCommand extends CreateForestsCommand {

    public CreateContentForestsCommand() {
        // Using the same default as /v1/rest-apis
        setForestsPerHost(3);
        setForestFilename("content-forest.json");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getAppConfig().isTestPortSet()) {
            setCreateTestForests(true);
        }
        super.execute(context);
    }

    @Override
    protected String getForestName(AppConfig appConfig, int forestNumber, boolean isTestDatabase) {
        String dbName = isTestDatabase ? appConfig.getTestContentDatabaseName() : appConfig.getContentDatabaseName();
        return dbName + "-" + forestNumber;
    }

    @Override
    protected String getForestDatabaseName(AppConfig appConfig, boolean isTestDatabase) {
        return isTestDatabase ? appConfig.getTestContentDatabaseName() : appConfig.getContentDatabaseName();
    }

}
