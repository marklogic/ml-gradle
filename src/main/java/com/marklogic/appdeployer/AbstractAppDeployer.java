package com.marklogic.appdeployer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.marklogic.clientutil.LoggingObject;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Abstract base class that just needs the subclass to define the list of AppPlugin instances to use. Handles executing
 * plugins in sorted order.
 */
public abstract class AbstractAppDeployer extends LoggingObject implements AppDeployer {

    private ManageClient manageClient;
    private AdminManager adminManager;

    public AbstractAppDeployer(ManageClient manageClient, AdminManager adminManager) {
        super();
        this.manageClient = manageClient;
        this.adminManager = adminManager;
    }

    protected abstract List<AppPlugin> getAppPlugins();

    public void deploy(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Deploying app %s with config dir of: %s", appConfig.getName(), configDir.getBaseDir()
                .getAbsolutePath()));

        List<AppPlugin> plugins = getAppPlugins();
        Collections.sort(plugins, new OnDeployComparator());

        AppPluginContext context = new AppPluginContext(appConfig, configDir, manageClient, adminManager);

        for (AppPlugin plugin : plugins) {
            logger.info(format("Invoking plugin [%s] with sort order [%d]", plugin.getClass().getName(),
                    plugin.getSortOrderOnDeploy()));
            plugin.onDeploy(context);
        }

        logger.info(format("Deployed app %s", appConfig.getName()));
    }

    public void undeploy(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Undeploying app %s with config dir: %s", appConfig.getName(), configDir.getBaseDir()
                .getAbsolutePath()));

        List<AppPlugin> plugins = getAppPlugins();
        Collections.sort(plugins, new OnUndeployComparator());

        for (AppPlugin plugin : plugins) {
            logger.info(format("Invoking plugin [%s] with sort order [%d]", plugin.getClass().getName(),
                    plugin.getSortOrderOnUndeploy()));

            AppPluginContext context = new AppPluginContext(appConfig, configDir, manageClient, adminManager);
            plugin.onUndeploy(context);
        }

        logger.info(format("Undeployed app %s", appConfig.getName()));
    }

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }
}

class OnDeployComparator implements Comparator<AppPlugin> {
    @Override
    public int compare(AppPlugin o1, AppPlugin o2) {
        return o1.getSortOrderOnDeploy().compareTo(o2.getSortOrderOnDeploy());
    }
}

class OnUndeployComparator implements Comparator<AppPlugin> {
    @Override
    public int compare(AppPlugin o1, AppPlugin o2) {
        return o1.getSortOrderOnUndeploy().compareTo(o2.getSortOrderOnUndeploy());
    }
}