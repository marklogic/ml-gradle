package com.rjrudin.marklogic.appdeployer.command.forests;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;

public class CreateContentForestsCommand extends CreateForestsCommand {

    public CreateContentForestsCommand() {
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
    protected String getForestDatabaseName(AppConfig appConfig, int forestNumber) {
        return appConfig.getContentDatabaseName() + "-" + forestNumber;
    }

}
