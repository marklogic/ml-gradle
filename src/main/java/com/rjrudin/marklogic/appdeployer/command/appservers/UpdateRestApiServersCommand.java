package com.rjrudin.marklogic.appdeployer.command.appservers;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.appservers.ServerManager;

public class UpdateRestApiServersCommand extends AbstractCommand {

    public UpdateRestApiServersCommand() {
        setExecuteSortOrder(SortOrderConstants.UPDATE_REST_API_SERVERS);
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
