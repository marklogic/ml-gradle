package com.marklogic.appdeployer.command.alert;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.alert.AlertRuleManager;

import java.io.File;

public class DeployAlertRulesCommand extends AbstractCommand {

	private String rulesDirectorySuffix = "-rules";
	private PayloadParser payloadParser = new PayloadParser();

	public DeployAlertRulesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_ALERT_RULES);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployRules(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployRules(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployRules(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		File configsDir = configDir.getAlertConfigsDir();
		if (configsDir != null && configsDir.exists()) {
			for (File f : configsDir.listFiles()) {
				if (f.isDirectory() && f.getName().endsWith(rulesDirectorySuffix)) {
					deployRulesInDirectory(f, context, databaseIdOrName);
				}
			}
		} else {
			logResourceDirectoryNotFound(configsDir);
		}
	}

	protected void deployRulesInDirectory(File dir, CommandContext context, String databaseIdOrName) {
		String configUri = extractConfigUriFromDirectory(dir);

		if (logger.isInfoEnabled()) {
			logger.info(format("Deploying alert rules with config URI '%s' in directory: %s", configUri,
				dir.getAbsolutePath()));
		}

		/**
		 * We have to build an AlertRuleManager each time, as we don't know the action name until we load the file and
		 * parse its contents.
		 */
		for (File f : listFilesInDirectory(dir)) {
			String payload = copyFileToString(f, context);
			String actionName = payloadParser.getPayloadFieldValue(payload, "action-name");
			AlertRuleManager mgr = new AlertRuleManager(context.getManageClient(), databaseIdOrName, configUri, actionName);
			saveResource(mgr, context, f);
		}
	}

	protected String extractConfigUriFromDirectory(File dir) {
		String name = dir.getName();
		return name.substring(0, name.length() - rulesDirectorySuffix.length());
	}

	public void setRulesDirectorySuffix(String targetDirectorySuffix) {
		this.rulesDirectorySuffix = targetDirectorySuffix;
	}

	public void setPayloadParser(PayloadParser payloadParser) {
		this.payloadParser = payloadParser;
	}
}
