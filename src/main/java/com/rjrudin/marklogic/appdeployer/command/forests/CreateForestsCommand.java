package com.rjrudin.marklogic.appdeployer.command.forests;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.SaveReceipt;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

public class CreateForestsCommand extends AbstractResourceCommand {

    public CreateForestsCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_FORESTS);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getBaseDir(), "forests");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ForestManager(context.getManageClient());
    }

    @Override
    protected SaveReceipt saveResource(ResourceManager mgr, CommandContext context, File f) {
        String payload = copyFileToString(f);

        AppConfig appConfig = context.getAppConfig();

        for (String hostName : new HostManager(context.getManageClient()).getHostNames()) {
            for (int i = 1; i <= 1; i++) {
                payload = tokenReplacer.replaceTokens(payload, appConfig, false);
                payload = payload.replace("%%FOREST_HOST%%", hostName);
                payload = payload.replace("%%FOREST_NAME%%", appConfig.getContentDatabaseName() + "-" + i);
                logger.info(payload);
                SaveReceipt receipt = mgr.save(payload);
                if (isStoreResourceIdsAsCustomTokens()) {
                    storeTokenForResourceId(receipt, context);
                }
            }
        }

        return null;
    }

}
