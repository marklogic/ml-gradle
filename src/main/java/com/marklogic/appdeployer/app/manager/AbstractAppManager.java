package com.marklogic.appdeployer.app.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.app.AppManager;
import com.marklogic.appdeployer.app.AppPlugin;
import com.marklogic.appdeployer.app.AppPluginContext;
import com.marklogic.appdeployer.app.ConfigDir;
import com.marklogic.clientutil.LoggingObject;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

/**
 * Abstract base class for managing an app that just needs the subclass to define the list of AppPlugin instances to
 * use. Handles executing plugins in sorted order
 */
public abstract class AbstractAppManager extends LoggingObject implements AppManager {

    private ManageClient manageClient;
    private AdminManager adminManager;

    public AbstractAppManager(ManageClient manageClient, AdminManager adminManager) {
        super();
        this.manageClient = manageClient;
        this.adminManager = adminManager;
    }

    protected abstract List<AppPlugin> getAppPlugins();

    public void createApp(AppConfig appConfig, ConfigDir configDir) {
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

    public void deleteApp(AppConfig appConfig, ConfigDir configDir) {
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