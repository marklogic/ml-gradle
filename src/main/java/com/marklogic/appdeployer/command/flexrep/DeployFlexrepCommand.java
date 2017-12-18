package com.marklogic.appdeployer.command.flexrep;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand;
import com.marklogic.appdeployer.command.cpf.DeployDomainsCommand;
import com.marklogic.appdeployer.command.cpf.DeployPipelinesCommand;
import com.marklogic.appdeployer.impl.SimpleAppDeployer;

import java.io.File;

/**
 * This command is for deploying all resources associated with a flexrep config. It combines CPF, flexrep configs
 * and targets, and optionally an HTTP server by reusing other commands. The intent is to support a configuration
 * for both a master and a replica in the same project. Most of the time you won't need this, in which case you
 * can just use DeployConfigsCommand and DeployTargetsCommand.
 */
public class DeployFlexrepCommand extends AbstractCommand implements UndoableCommand {

    public DeployFlexrepCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_SERVERS);
    }

    @Override
    public Integer getUndoSortOrder() {
        return SortOrderConstants.DELETE_OTHER_SERVERS;
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig appConfig = context.getAppConfig();
        File flexrepBaseDir = getFlexrepBaseDir(appConfig);
        if (flexrepBaseDir != null) {
            SimpleAppDeployer d = new SimpleAppDeployer(context.getManageClient(), context.getAdminManager(),
                    new DeployCpfConfigsCommand(), new DeployDomainsCommand(), new DeployPipelinesCommand(), new DeployConfigsCommand(), new DeployTargetsCommand(), new DeployOtherServersCommand());

            for (ConfigDir configDir : appConfig.getConfigDirs()) {
	            File currentBaseDir = configDir.getBaseDir();
	            configDir.setBaseDir(flexrepBaseDir);
	            try {
		            d.deploy(appConfig);
	            } finally {
		            configDir.setBaseDir(currentBaseDir);
	            }
            }
        }
    }


    @Override
    public void undo(CommandContext context) {
        AppConfig appConfig = context.getAppConfig();
        File flexrepBaseDir = getFlexrepBaseDir(appConfig);
        if (flexrepBaseDir != null) {
            SimpleAppDeployer d = new SimpleAppDeployer(context.getManageClient(), context.getAdminManager(), new DeployOtherServersCommand());

            for (ConfigDir configDir : appConfig.getConfigDirs()) {
	            File currentBaseDir = configDir.getBaseDir();
	            configDir.setBaseDir(flexrepBaseDir);
	            try {
		            d.undeploy(appConfig);
	            } finally {
		            configDir.setBaseDir(currentBaseDir);
	            }
            }
        }
    }

    protected File getFlexrepBaseDir(AppConfig appConfig) {
        String path = appConfig.getFlexrepPath();
        if (path == null) {
            return null;
        }

	    /**
	     * Little trickier in 3.3.0 - gotta check every ConfigDir to see if a flexrep base directory exists. Last
	     * one wins.
	     */
	    File flexrepBaseDir = null;
        for (ConfigDir configDir : appConfig.getConfigDirs()) {
	        File flexrepDir = configDir.getFlexrepDir();
	        if (flexrepDir == null || !flexrepDir.exists()) {
		        continue;
	        }

	        File tmp = new File(flexrepDir, path);
	        if (tmp != null && tmp.exists()) {
		        flexrepBaseDir = tmp;
	        }
        }
        return flexrepBaseDir;
    }
}
