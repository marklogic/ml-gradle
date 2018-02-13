package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;

import java.io.File;

public abstract class AbstractCpfResourceCommand extends AbstractCommand {

    protected abstract File getCpfResourceDir(ConfigDir configDir);

    protected abstract AbstractCpfResourceManager getResourceManager(CommandContext context, String databaseIdOrName);

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();
        for (ConfigDir configDir : config.getConfigDirs()) {
	        File dir = getCpfResourceDir(configDir);
	        if (dir.exists()) {
		        AbstractCpfResourceManager mgr = getResourceManager(context, config.getTriggersDatabaseName());
		        for (File f : listFilesInDirectory(dir)) {
			        String payload = copyFileToString(f, context);
			        mgr.save(payload);
		        }
	        } else {
		        logResourceDirectoryNotFound(dir);
	        }
        }
    }
}
