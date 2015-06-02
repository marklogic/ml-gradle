package com.marklogic.appdeployer.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.AppPlugin;
import com.marklogic.appdeployer.AppPluginContext;
import com.marklogic.appdeployer.ConfigDir;
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
        logger.info(format("Creating application %s with config dir of: %s", appConfig.getName(), configDir
                .getBaseDir().getAbsolutePath()));

        List<AppPlugin> plugins = getAppPlugins();
        Collections.sort(plugins, new OnCreateComparator());

        AppPluginContext context = new AppPluginContext(appConfig, configDir, manageClient, adminManager);

        for (AppPlugin plugin : plugins) {
            logger.info(format("Invoking plugin [%s] with sort order [%d]", plugin.getClass().getName(),
                    plugin.getSortOrderOnCreate()));
            plugin.onCreate(context);
        }

        logger.info(format("Created application %s", appConfig.getName()));
    }

    public void undeploy(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Deleting app %s with config dir: %s", appConfig.getName(), configDir.getBaseDir()
                .getAbsolutePath()));

        List<AppPlugin> plugins = getAppPlugins();
        Collections.sort(plugins, new OnDeleteComparator());

        for (AppPlugin plugin : plugins) {
            logger.info(format("Invoking plugin [%s] with sort order [%d]", plugin.getClass().getName(),
                    plugin.getSortOrderOnDelete()));

            AppPluginContext context = new AppPluginContext(appConfig, configDir, manageClient, adminManager);
            plugin.onDelete(context);
        }

        logger.info(format("Finished deleting app %s", appConfig.getName()));
    }

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }
}

class OnCreateComparator implements Comparator<AppPlugin> {
    @Override
    public int compare(AppPlugin o1, AppPlugin o2) {
        return o1.getSortOrderOnCreate().compareTo(o2.getSortOrderOnCreate());
    }
}

class OnDeleteComparator implements Comparator<AppPlugin> {
    @Override
    public int compare(AppPlugin o1, AppPlugin o2) {
        return o1.getSortOrderOnDelete().compareTo(o2.getSortOrderOnDelete());
    }
}