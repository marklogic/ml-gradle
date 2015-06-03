package com.marklogic.appdeployer.plugin.servers;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppPluginContext;
import com.marklogic.appdeployer.plugin.AbstractPlugin;
import com.marklogic.rest.mgmt.appservers.ServerManager;

public class UpdateRestApiServersPlugin extends AbstractPlugin {

    @Override
    public Integer getSortOrderOnDeploy() {
        return 600;
    }

    @Override
    public void onDeploy(AppPluginContext context) {
        File f = context.getConfigDir().getRestApiServerFile();
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
