package com.rjrudin.marklogic.appdeployer.command.flexrep;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.flexrep.TargetManager;

/**
 * The directory structure for this is a bit different from most command. Since targets belong to a certain flexrep
 * config, this command looks for every directory under flexrep/configs that ends with "-targets". For each such
 * directory, the flexrep config name is determined by stripping "-targets" off the directory name, and then each target
 * JSON/XML file in the directory is loaded for that flexrep config name.
 */
public class DeployTargetsCommand extends AbstractResourceCommand {

    private String databaseIdOrName;
    private String configIdOrName;

    public DeployTargetsCommand(String configIdOrName) {
        this.configIdOrName = configIdOrName;
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getFlexrepDir(), "configs") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        String db = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
        return new TargetManager(context.getManageClient(), db, configIdOrName);
    }

    public void setDatabaseIdOrName(String databaseIdOrName) {
        this.databaseIdOrName = databaseIdOrName;
    }
}
