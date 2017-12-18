package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;

import java.io.File;

public abstract class AbstractCpfResourceCommand extends AbstractCommand {

    protected abstract String getCpfDirectoryName();

    protected abstract AbstractCpfResourceManager getResourceManager(CommandContext context);

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();
        for (ConfigDir configDir : config.getConfigDirs()) {
	        File dir = new File(configDir.getCpfDir(), getCpfDirectoryName());
	        if (dir.exists()) {
		        AbstractCpfResourceManager mgr = getResourceManager(context);
		        for (File f : listFilesInDirectory(dir)) {
			        String payload = copyFileToString(f, context);
			        mgr.save(config.getTriggersDatabaseName(), payload);
		        }
	        }
        }
    }
}
