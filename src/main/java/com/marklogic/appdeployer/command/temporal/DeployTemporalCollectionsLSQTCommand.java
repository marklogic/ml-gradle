package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.temporal.TemporalCollectionLSQTManager;

import java.io.File;

/**
 * Created by dsmyth on 28/02/2017.
 */
public class DeployTemporalCollectionsLSQTCommand  extends AbstractCommand {

	private String databaseIdOrName;

	public DeployTemporalCollectionsLSQTCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_TEMPORAL_COLLECTIONS_LSQT);
	}

	public void setDatabaseIdOrName(String databaseIdOrName) {
		this.databaseIdOrName = databaseIdOrName;
	}

	@Override
	public void execute(CommandContext context) {
		File configDir = new File(new File(context.getAppConfig().getConfigDir().getTemporalDir(), "collections"), "lsqt");
		String db = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
		if (configDir != null && configDir.exists()) {
			for (File f : configDir.listFiles(new ResourceFilenameFilter())) {
				String name = f.getName();
				// use filename without suffix as temporal collection
				String temporalCollectionName = name.replaceAll(".xml|.json","");
				String payload = copyFileToString(f, context);
				logger.info(format("Extracted temporal collection name '%s' from filename '%s'", temporalCollectionName, name));
				new TemporalCollectionLSQTManager(context.getManageClient(),db, temporalCollectionName).save(payload);
			}
		}
	}
}
