package com.marklogic.appdeployer.project;

import java.io.File;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.AdminConfig;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.services.ServiceManager;
import com.marklogic.appdeployer.util.RestTemplateUtil;

/**
 * Manages a project - i.e. looks for files in the ConfigDir and makes the appropriate calls to the Mgmt API using
 * "NounManager" classes. This is the class that something like a Gradle plugin would interact with, and hopefully only
 * this class.
 */
public class ProjectManager extends AbstractManager {

    private ManageClient manageClient;
    private ApplicationContext appContext;
    private AdminConfig adminConfig;
    private int waitForRestartCheckInterval = 500;

    public ProjectManager(ApplicationContext appContext, ManageClient manageClient) {
        this.appContext = appContext;
        this.manageClient = manageClient;
    }

    public void createApp(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Creating application %s with config dir of: %s", appConfig.getName(), configDir
                .getBaseDir().getAbsolutePath()));
        createRestApi(configDir, appConfig);

        for (ProjectPlugin plugin : appContext.getBeansOfType(ProjectPlugin.class).values()) {
            logger.info(format("Invoking project plugin %s", plugin.getClass()));
            plugin.onCreate(appConfig, configDir, manageClient);
        }

        logger.info(format("Created application %s", appConfig.getName()));
    }

    public void createRestApi(ConfigDir configDir, AppConfig config) {
        File f = configDir.getRestApiFile();
        String payload = copyFileToString(f);

        ServiceManager mgr = new ServiceManager(manageClient);

        payload = replaceConfigTokens(payload, config, false);
        mgr.createRestApi(config.getRestServerName(), payload);

        if (config.isTestPortSet()) {
            payload = replaceConfigTokens(payload, config, true);
            mgr.createRestApi(config.getTestRestServerName(), payload);
        }
    }

    public void updateDatabases(ConfigDir configDir, AppConfig config) {
        File f = configDir.getContentDatabaseFile();
        if (f.exists()) {

        } else {
            logger.info("Not updating content databases, no database file found at: " + f.getAbsolutePath());
        }
    }

    protected String replaceConfigTokens(String payload, AppConfig config, boolean isTestResource) {
        payload = payload.replace("%%NAME%%",
                isTestResource ? config.getTestRestServerName() : config.getRestServerName());
        payload = payload.replace("%%GROUP%%", config.getGroupName());
        payload = payload.replace("%%DATABASE%%",
                isTestResource ? config.getTestContentDatabaseName() : config.getContentDatabaseName());
        payload = payload.replace("%%MODULES-DATABASE%%", config.getModulesDatabaseName());
        payload = payload.replace("%%TRIGGERS_DATABASE%%", config.getTriggersDatabaseName());
        payload = payload.replace("%%PORT%%", isTestResource ? config.getTestRestPort().toString() : config
                .getRestPort().toString());
        return payload;
    }

    public void deleteRestApiAndWaitForRestart(AppConfig appConfig, boolean includeModules, boolean includeContent) {
        String timestamp = getLastRestartTimestamp();
        logger.info("About to delete REST API, will then wait for MarkLogic to restart");
        deleteRestApi(appConfig, includeModules, includeContent);
        waitForRestart(timestamp);
    }

    /**
     * This is the primary method for deleting an entire app. That means it'll need knowledge of everything created by
     * the app, and not just the REST API.
     */
    public void deleteApp(AppConfig appConfig, ConfigDir configDir) {
        logger.info(format("Deleting app %s with config dir: %s", appConfig.getName(), configDir.getBaseDir()
                .getAbsolutePath()));

        deleteRestApiAndWaitForRestart(appConfig, true, true);

        for (ProjectPlugin plugin : appContext.getBeansOfType(ProjectPlugin.class).values()) {
            logger.info(format("Invoking project plugin %s", plugin.getClass()));
            plugin.onDelete(appConfig, configDir, manageClient);
        }

        logger.info(format("Finished deleting app %s", appConfig.getName()));
    }

    public void deleteRestApi(AppConfig appConfig, boolean includeModules, boolean includeContent) {
        String path = manageClient.getBaseUrl() + "/v1/rest-apis/" + appConfig.getName() + "?";
        if (includeModules) {
            path += "include=modules&";
        }
        if (includeContent) {
            path += "include=content";
        }
        logger.info("Deleting REST API, path: " + path);
        manageClient.getRestTemplate().exchange(path, HttpMethod.DELETE, null, String.class);
        logger.info("Finished deleting REST API");
    }

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

    protected String copyFileToString(File f) {
        try {
            return new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
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
