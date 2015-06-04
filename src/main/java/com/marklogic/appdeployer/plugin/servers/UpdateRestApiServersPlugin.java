package com.marklogic.appdeployer.plugin.servers;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppPluginContext;
import com.marklogic.appdeployer.plugin.AbstractPlugin;
import com.marklogic.appdeployer.plugin.SortOrderConstants;
import com.marklogic.rest.mgmt.appservers.ServerManager;

public class UpdateRestApiServersPlugin extends AbstractPlugin {

    @Override
    public Integer getSortOrderOnDeploy() {
        return SortOrderConstants.UPDATE_REST_API_SERVERS_ORDER;
    }

    /**
     * This uses a different file than that of creating a REST API, as the payload for /v1/rest-apis differs from that
     * of the /manage/v2/servers endpoint.
     */
    @Override
    public void onDeploy(AppPluginContext context) {
        File f = context.getAppConfig().getConfigDir().getRestApiServerFile();
        if (f.exists()) {
            ServerManager mgr = new ServerManager(context.getManageClient());

            String payload = copyFileToString(f);
            AppConfig appConfig = context.getAppConfig();

            String json = tokenReplacer.replaceTokens(payload, appConfig, false);
            mgr.updateServer(appConfig.getRestServerName(), appConfig.getGroupName(), json);

            if (appConfig.isTestPortSet()) {
                json = tokenReplacer.replaceTokens(payload, appConfig, true);
                mgr.updateServer(appConfig.getTestRestServerName(), appConfig.getGroupName(), json);
            }
        } else {
            logger.info(format("No REST API server file found at %s, so not updating the server", f.getAbsolutePath()));
        }

    }

    @Override
    public void onUndeploy(AppPluginContext context) {
    }

}
