package com.marklogic.appdeployer.command.appservers;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.appservers.ServerManager;

public class UpdateRestApiServersCommand extends AbstractCommand {

    public UpdateRestApiServersCommand() {
        setExecuteSortOrder(SortOrderConstants.UPDATE_REST_API_SERVERS_ORDER);
    }

    /**
     * This uses a different file than that of creating a REST API, as the payload for /v1/rest-apis differs from that
     * of the /manage/v2/servers endpoint.
     */
    @Override
    public void execute(CommandContext context) {
        File f = context.getAppConfig().getConfigDir().getRestApiServerFile();
        if (f.exists()) {
            AppConfig appConfig = context.getAppConfig();

            ServerManager mgr = new ServerManager(context.getManageClient(), appConfig.getGroupName());

            String payload = copyFileToString(f);

            String json = tokenReplacer.replaceTokens(payload, appConfig, false);
            mgr.save(json);

            if (appConfig.isTestPortSet()) {
                json = tokenReplacer.replaceTokens(payload, appConfig, true);
                mgr.save(json);
            }
        } else {
            logger.info(format("No REST API server file found at %s, so not updating the server", f.getAbsolutePath()));
        }
    }

}
