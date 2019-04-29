package com.marklogic.appdeployer.command.appservers;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.appservers.ServerManager;

import java.io.File;

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
		if (f != null && f.exists()) {
			AppConfig appConfig = context.getAppConfig();

			ServerManager mgr = new ServerManager(context.getManageClient(), appConfig.getGroupName());

			saveResource(mgr, context, f);

			if (appConfig.isTestPortSet()) {
				String payload = copyFileToString(f);
				payload = payloadTokenReplacer.replaceTokens(payload, appConfig, true);
				payload = adjustPayloadBeforeSavingResource(context, f, payload);
				mgr.save(payload);
			}
		}
	}

	protected File findRestApiConfigFile(CommandContext context) {
		if (restApiFilename != null) {
			File f = null;
			for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
				File tmpFile = new File(configDir.getBaseDir(), restApiFilename);
				if (tmpFile.exists()) {
					f = tmpFile;
					if (logger.isInfoEnabled()) {
						logger.info("Found REST API configuration file at: " + f.getAbsolutePath());
					} else if (logger.isInfoEnabled()) {
						logger.info("Did not find REST API configuration file at: " + tmpFile.getAbsolutePath());
					}
				}
			}
			return f;
		} else {
			File f = null;
			for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
				File tmpFile = configDir.getRestApiServerFile();
				if (tmpFile.exists()) {
					f = tmpFile;
					if (logger.isInfoEnabled()) {
						logger.info("Found REST API configuration file at: " + f.getAbsolutePath());
					}
				} else if (logger.isInfoEnabled()) {
					logger.info("Did not find REST API configuration file at: " + tmpFile.getAbsolutePath());
				}
			}
			return f;
		}
	}
}
