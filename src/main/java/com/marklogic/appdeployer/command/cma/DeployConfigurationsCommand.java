package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.cma.ConfigurationManager;

import java.io.File;

/**
 * The /manage/v3 docs indicate that zips can be uploaded, but it's not clear what the format of those zips should be.
 * So for now, this just supports JSON and XML, which seem like the most likely formats to use.
 */
public class DeployConfigurationsCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context) {
		ConfigurationManager mgr = new ConfigurationManager(context.getManageClient());

		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File dir = configDir.getConfigurationsDir();
			if (dir != null && dir.exists()) {
				if (logger.isInfoEnabled()) {
					logger.info("Processing files in directory: " + dir.getAbsolutePath());
				}
				for (File f : super.listFilesInDirectory(dir)) {
					if (logger.isInfoEnabled()) {
						logger.info("Processing file: " + f.getAbsolutePath());
					}
					String payload = readResourceFromFile(context, f);
					SaveReceipt receipt = mgr.save(payload);
					afterResourceSaved(null, context, new ResourceReference(f, null), receipt);
				}
			} else {
				logResourceDirectoryNotFound(dir);
			}
		}
	}
}
