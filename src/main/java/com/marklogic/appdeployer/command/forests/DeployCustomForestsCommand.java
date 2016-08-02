package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.forests.ForestManager;

import java.io.File;

/**
 * Use this command when you want precise control over the forests that are created for a database. It processes
 * each directory under ml-config/forests (the name of the directory does not matter, but it makes sense to name
 * it after the database that the forests belong to), and each file in a directory can have a single forest object
 * or an array of forest objects.
 */
public class DeployCustomForestsCommand extends AbstractCommand {

	public DeployCustomForestsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FORESTS);
	}

	@Override
	public void execute(CommandContext context) {
		File dir = new File(context.getAppConfig().getConfigDir().getBaseDir(), "forests");
		if (dir != null && dir.exists()) {
			for (File f : dir.listFiles()) {
				if (f.isDirectory()) {
					processDirectory(f, context);
				}
			}
		}
	}

	protected void processDirectory(File dir, CommandContext context) {
		ForestManager mgr = new ForestManager(context.getManageClient());
		for (File f : listFilesInDirectory(dir)) {
			String payload = copyFileToString(f, context);
			mgr.saveJsonForests(payload);
		}
	}
}
