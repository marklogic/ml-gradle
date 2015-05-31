package com.marklogic.appdeployer.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.AdminConfig;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.util.RestTemplateUtil;

/**
 * Manages a project - i.e. looks for files in the ConfigDir and makes the appropriate calls to the Mgmt API using
 * "NounManager" classes. This is the class that something like a Gradle plugin would interact with, and hopefully only
 * this class.
 */
public class ProjectManager extends AbstractManager {

    private ManageClient manageClient;
    private ApplicationContext appContext;

    private int waitForRestartCheckInterval = 500;
    private AdminConfig adminConfig;

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
                logger.info("Plugin requests restart after onDelete, last restart timestamp: " + lastRestartTimestamp);
                lastRestartTimestamp = getLastRestartTimestamp();
            }
            plugin.onDelete(appConfig, configDir, manageClient);
            if (lastRestartTimestamp != null) {
                waitForRestart(lastRestartTimestamp);
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

    /**
     * TODO Move this to an AdminManager
     * 
     * @param lastRestartTimestamp
     */
    public void waitForRestart(String lastRestartTimestamp) {
        logger.info("Waiting for MarkLogic to restart, last restart timestamp: " + lastRestartTimestamp);
        logger.info("Ignore any HTTP client logging about socket exceptions and retries, those are expected while waiting for MarkLogic to restart");
        try {
            while (true) {
                sleepUntilNextRestartCheck();
                String restart = getLastRestartTimestamp();
                if (restart != null && !restart.equals(lastRestartTimestamp)) {
                    logger.info(String
                            .format("MarkLogic has successfully restarted; new restart timestamp [%s] is greater than last restart timestamp [%s]",
                                    restart, lastRestartTimestamp));
                    break;
                }
            }
        } catch (Exception e) {
            String message = "Caught exception while waiting for MarkLogic to restart: " + e.getMessage();
            if (logger.isDebugEnabled()) {
                logger.warn(message, e);
            } else {
                logger.warn(message);
            }
        }
    }

    protected void sleepUntilNextRestartCheck() {
        try {
            Thread.sleep(getWaitForRestartCheckInterval());
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * TODO May want to extract this into an AdminManager that depends on AdminConfig.
     */
    public String getLastRestartTimestamp() {
        if (adminConfig == null) {
            throw new IllegalStateException("Cannot access admin app, no admin config provided");
        }
        RestTemplate t = RestTemplateUtil.newRestTemplate(adminConfig);
        return t.getForEntity(adminConfig.getBaseUrl() + "/admin/v1/timestamp", String.class).getBody();
    }

    public AdminConfig getAdminConfig() {
        return adminConfig;
    }

    public void setAdminConfig(AdminConfig adminConfig) {
        this.adminConfig = adminConfig;
    }

    public int getWaitForRestartCheckInterval() {
        return waitForRestartCheckInterval;
    }

    public void setWaitForRestartCheckInterval(int waitForRestartCheckInterval) {
        this.waitForRestartCheckInterval = waitForRestartCheckInterval;
    }

}
