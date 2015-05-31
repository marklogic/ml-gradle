package com.marklogic.appdeployer.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.admin.AdminManager;

/**
 * Manages a project - i.e. looks for files in the ConfigDir and makes the appropriate calls to the Mgmt API using
 * "NounManager" classes. This is the class that something like a Gradle plugin would interact with, and hopefully only
 * this class.
 */
public class ProjectManager extends AbstractManager {

    private ManageClient manageClient;
    private ApplicationContext appContext;
    private AdminManager adminManager;

    public ProjectManager(ApplicationContext appContext, ManageClient manageClient) {
        this.appContext = appContext;
        this.manageClient = manageClient;
    }

    public void createApp(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Creating application %s with config dir of: %s", appConfig.getName(), configDir
                .getBaseDir().getAbsolutePath()));

        for (ProjectPlugin plugin : getPluginsFromSpring(appContext)) {
            logPluginToInvoke(plugin);
            plugin.onCreate(appConfig, configDir, manageClient);
        }

        logger.info(format("Created application %s", appConfig.getName()));
    }

    public void deleteApp(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Deleting app %s with config dir: %s", appConfig.getName(), configDir.getBaseDir()
                .getAbsolutePath()));

        for (ProjectPlugin plugin : getPluginsFromSpring(appContext)) {
            logPluginToInvoke(plugin);

            String lastRestartTimestamp = null;
            if (plugin instanceof RequirestRestartOnDelete) {
                if (adminManager != null) {
                    lastRestartTimestamp = adminManager.getLastRestartTimestamp();
                } else {
                    logger.warn("No AdminManager set, cannot get last restart timestamp");
                }
            }

            plugin.onDelete(appConfig, configDir, manageClient);

            if (lastRestartTimestamp != null) {
                if (adminManager != null) {
                    adminManager.waitForRestart(lastRestartTimestamp);
                } else {
                    logger.warn("No AdminManager set, cannot wait for MarkLogic to restart");
                }
            }
        }

        logger.info(format("Finished deleting app %s", appConfig.getName()));
    }

    private void logPluginToInvoke(ProjectPlugin plugin) {
        logger.info(format("Invoking plugin [%s] with sort order [%d]", plugin.getClass().getName(),
                plugin.getSortOrder()));
    }

    protected List<ProjectPlugin> getPluginsFromSpring(ApplicationContext applicationContext) {
        List<ProjectPlugin> plugins = new ArrayList<ProjectPlugin>();
        plugins.addAll(applicationContext.getBeansOfType(ProjectPlugin.class).values());
        Collections.sort(plugins);
        return plugins;
    }

    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

}
