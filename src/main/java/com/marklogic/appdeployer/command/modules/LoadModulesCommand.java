package com.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.CommandContext;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.clientutil.modulesloader.ModulesLoader;
import com.marklogic.clientutil.modulesloader.impl.DefaultModulesLoader;

public class LoadModulesCommand extends AbstractCommand {

    private ModulesLoader modulesLoader;

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.LOAD_MODULES_ORDER;
    }

    @Override
    public void execute(CommandContext context) {
        if (modulesLoader == null) {
            modulesLoader = new DefaultModulesLoader();
        }
        AppConfig config = context.getAppConfig();
        DatabaseClient client = DatabaseClientFactory.newClient(config.getHost(), config.getRestPort(),
                config.getUsername(), config.getPassword(), config.getAuthentication());
        for (String modulesPath : config.getModulePaths()) {
            logger.info("Loading modules from dir: " + modulesPath);
            modulesLoader.loadModules(new File(modulesPath), client);
        }
    }

    @Override
    public void undo(CommandContext context) {
    }

    public void setModulesLoader(ModulesLoader modulesLoader) {
        this.modulesLoader = modulesLoader;
    }
}
