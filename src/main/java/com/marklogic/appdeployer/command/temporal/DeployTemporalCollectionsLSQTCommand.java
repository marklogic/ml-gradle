package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.*;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionLSQTManager;

import java.io.File;

public class DeployTemporalCollectionsLSQTCommand extends AbstractCommand {

	public DeployTemporalCollectionsLSQTCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_TEMPORAL_COLLECTIONS_LSQT);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployTemporalCollectionsLsqt(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				deployTemporalCollectionsLsqt(context, new ConfigDir(dir), dir.getName());
			}
		}
	}

	protected void deployTemporalCollectionsLsqt(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		File dir = configDir.getTemporalCollectionsLsqtDir();
		if (dir != null && dir.exists()) {
			for (File f : dir.listFiles(new ResourceFilenameFilter())) {
				String name = f.getName();
				// use filename without suffix as temporal collection
				String temporalCollectionName = name.replaceAll(".xml|.json", "");
				String payload = copyFileToString(f, context);
				if (logger.isInfoEnabled()) {
					logger.info(format("Extracted temporal collection name '%s' from filename '%s'", temporalCollectionName, name));
				}
				new TemporalCollectionLSQTManager(context.getManageClient(), databaseIdOrName, temporalCollectionName).save(payload);
			}
		}

	}
}
