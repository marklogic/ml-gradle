package com.marklogic.appdeployer.command.cpf;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.rest.mgmt.cpf.AbstractCpfResourceManager;

public abstract class AbstractCpfResourceCommand extends AbstractCommand {

    protected abstract String getCpfDirectoryName();

    protected abstract AbstractCpfResourceManager getResourceManager(CommandContext context);

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();
        File dir = new File(config.getConfigDir().getCpfDir(), getCpfDirectoryName());
        if (dir.exists()) {
            AbstractCpfResourceManager mgr = getResourceManager(context);
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith(".json") || f.getName().endsWith(".xml")) {
                    String payload = copyFileToString(f);
                    payload = tokenReplacer.replaceTokens(payload, config, false);
                    mgr.save(config.getTriggersDatabaseName(), payload);
                }
            }
        }
    }
}
