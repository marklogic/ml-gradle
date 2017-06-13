package com.marklogic.appdeployer.command.appservers;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.appservers.ServerManager;

/**
 * Command for updating an existing REST API server that was presumably created via /v1/rest-apis.
 */
public class UpdateRestApiServersCommand extends AbstractCommand {

    private String restApiFilename;

    public UpdateRestApiServersCommand() {
        setExecuteSortOrder(SortOrderConstants.UPDATE_REST_API_SERVERS);
    }

    public UpdateRestApiServersCommand(String restApiFilename) {
        this();
        this.restApiFilename = restApiFilename;
    }

    /**
     * This uses a different file than that of creating a REST API, as the payload for /v1/rest-apis differs from that
     * of the /manage/v2/servers endpoint.
     */
    @Override
    public void execute(CommandContext context) {
        File f = findRestApiConfigFile(context);
        if (f.exists()) {
            AppConfig appConfig = context.getAppConfig();

            ServerManager mgr = new ServerManager(context.getManageClient(), appConfig.getGroupName());

            String payload = copyFileToString(f);

            String json = payloadTokenReplacer.replaceTokens(payload, appConfig, false);
            mgr.save(json);

            if (appConfig.isTestPortSet()) {
                json = payloadTokenReplacer.replaceTokens(payload, appConfig, true);
                mgr.save(json);
            }
        } else {
            logger.info(format("No REST API server file found at %s, so not updating the server", f.getAbsolutePath()));
        }
    }

    protected File findRestApiConfigFile(CommandContext context) {
        if (restApiFilename != null) {
            return new File(context.getAppConfig().getConfigDir().getBaseDir(), restApiFilename);
        } else {
            return context.getAppConfig().getConfigDir().getRestApiServerFile();
        }
    }
}
