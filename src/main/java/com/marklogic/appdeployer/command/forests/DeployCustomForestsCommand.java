package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.forests.ForestManager;

import java.io.File;

/**
 * Use this command when you want precise control over the forests that are created for a database. It processes
 * each directory under ml-config/forests (the name of the directory does not matter, but it makes sense to name
 * it after the database that the forests belong to), and each file in a directory can have a single forest object
 * or an array of forest objects.
 *
 * You can also set customForestsPath to specify a directory other than "forest" as the path that contains
 * directories of custom forests. This allows you to easily support different custom forests in different
 * environments.
 */
public class DeployCustomForestsCommand extends AbstractCommand {

	private String customForestsPath = "forests";
	private PayloadParser payloadParser;

	public DeployCustomForestsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FORESTS);
	}

	@Override
	public void execute(CommandContext context) {
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File dir = new File(configDir.getBaseDir(), customForestsPath);
			if (dir != null && dir.exists()) {
				payloadParser = new PayloadParser();
				for (File f : dir.listFiles()) {
					if (f.isDirectory()) {
						processDirectory(f, context);
					}
				}
			} else {
				logResourceDirectoryNotFound(dir);
			}
		}
	}

	/**
	 * Supports JSON files with a single payload or an array of payloads, or an XML file with a single payload.
	 *
	 * @param dir
	 * @param context
	 */
	protected void processDirectory(File dir, CommandContext context) {
		if (logger.isInfoEnabled()) {
			logger.info("Processing custom forest files in directory: " + dir.getAbsolutePath());
		}
		ForestManager mgr = new ForestManager(context.getManageClient());
		for (File f : listFilesInDirectory(dir)) {
			if (logger.isInfoEnabled()) {
				logger.info("Processing forests in file: " + f.getAbsolutePath());
			}
			String payload = readResourceFromFile(context, f);
			if (payloadParser.isJsonPayload(payload)) {
				mgr.saveJsonForests(payload);
			} else {
				mgr.save(payload);
			}
		}
	}

	public void setCustomForestsPath(String customForestsPath) {
		this.customForestsPath = customForestsPath;
	}
}
