package com.marklogic.appdeployer.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.admin.AdminManager;

/**
 * Manages creating and deleting an app - i.e. looks for files in the ConfigDir and makes the appropriate calls to the
 * Mgmt API using "NounManager" classes. This is the class that something like a Gradle plugin would interact with, and
 * hopefully only this class.
 */
public class AppManager extends AbstractManager {

    private ManageClient manageClient;
    private ApplicationContext appContext;
    private AdminManager adminManager;

    public AppManager(ApplicationContext appContext, ManageClient manageClient, AdminManager adminManager) {
        this.appContext = appContext;
        this.manageClient = manageClient;
        this.adminManager = adminManager;
    }

    public void createApp(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Creating application %s with config dir of: %s", appConfig.getName(), configDir
                .getBaseDir().getAbsolutePath()));

        List<AppPlugin> plugins = getPluginsFromSpring(appContext);
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

        List<AppPlugin> plugins = getPluginsFromSpring(appContext);
        Collections.sort(plugins, new OnDeleteComparator());

        for (AppPlugin plugin : plugins) {
            logger.info(format("Invoking plugin [%s] with sort order [%d]", plugin.getClass().getName(),
                    plugin.getSortOrderOnDelete()));

            AppPluginContext context = new AppPluginContext(appConfig, configDir, manageClient, adminManager);
            plugin.onDelete(context);
        }

        logger.info(format("Finished deleting app %s", appConfig.getName()));
    }

    protected List<AppPlugin> getPluginsFromSpring(ApplicationContext applicationContext) {
        List<AppPlugin> plugins = new ArrayList<AppPlugin>();
        plugins.addAll(applicationContext.getBeansOfType(AppPlugin.class).values());
        return plugins;
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