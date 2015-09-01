package com.rjrudin.marklogic.appdeployer.command.flexrep;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.flexrep.ConfigManager;

/**
 * Defaults to the content database name in the AppConfig instance. Can be overridden via the databaseNameOrId property.
 */
public class DeployConfigsCommand extends AbstractResourceCommand {

    private String databaseIdOrName;

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getFlexrepDir(), "configs") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        String db = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
        return new ConfigManager(context.getManageClient(), db);
    }

    public void setDatabaseIdOrName(String databaseIdOrName) {
        this.databaseIdOrName = databaseIdOrName;
    }

}
