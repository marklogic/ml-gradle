package com.marklogic.appdeployer.mgmt;

import java.io.File;
import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.services.ServiceManager;
import com.marklogic.appdeployer.util.RestTemplateUtil;
import com.marklogic.clientutil.LoggingObject;

/**
 * This is an "uber" manager that provides high-level methods that depend on NounManager classes for getting things
 * done.
 */
public class ConfigManager extends LoggingObject {

    private ManageClient client;
    private AdminConfig adminConfig;
    private int waitForRestartCheckInterval = 500;

    public ConfigManager(ManageClient client) {
        this.client = client;
    }

    public void createRestApi(ConfigDir configDir, AppConfig config) {
        File f = configDir.getRestApiFile();
        String input = copyFileToString(f);

        ServiceManager mgr = new ServiceManager(client);

        String body = replaceRestApiTokens(input, config);
        mgr.createRestApi(config.getRestServerName(), body);

        if (config.isTestPortSet()) {
            body = replaceRestApiTokens(input, config);
            mgr.createRestApi(config.getTestRestServerName(), body);
        }
    }

    public void updateDatabases(ConfigDir configDir, AppConfig config) {
        File f = configDir.getContentDatabaseFile();
        if (f.exists()) {

        } else {
            logger.info("Not updating content databases, no database file found at: " + f.getAbsolutePath());
        }
    }

    protected String replaceRestApiTokens(String input, AppConfig config) {
        input = input.replace("%%NAME%%", config.getRestServerName());
        input = input.replace("%%GROUP%%", config.getGroupName());
        input = input.replace("%%DATABASE%%", config.getContentDatabaseName());
        input = input.replace("%%MODULES-DATABASE%%", config.getModulesDatabaseName());
        input = input.replace("%%PORT%%", config.getRestPort() + "");
        return input;
    }

    protected String replaceTestRestApiTokens(String input, AppConfig config) {
        input = input.replace("%%NAME%%", config.getTestRestServerName());
        input = input.replace("%%GROUP%%", config.getGroupName());
        input = input.replace("%%DATABASE%%", config.getTestContentDatabaseName());
        input = input.replace("%%MODULES-DATABASE%%", config.getModulesDatabaseName());
        input = input.replace("%%PORT%%", config.getTestRestPort() + "");
        return input;
    }

    public void deleteRestApiAndWaitForRestart(AppConfig config, boolean includeModules, boolean includeContent) {
        String timestamp = getLastRestartTimestamp();
        logger.info("About to delete REST API, will then wait for MarkLogic to restart");
        deleteRestApi(config, includeModules, includeContent);
        waitForRestart(timestamp);
    }

    public void deleteRestApi(AppConfig config, boolean includeModules, boolean includeContent) {
        String path = client.getBaseUrl() + "/v1/rest-apis/" + config.getName() + "?";
        if (includeModules) {
            path += "include=modules&";
        }
        if (includeContent) {
            path += "include=content";
        }
        logger.info("Deleting app, path: " + path);
        client.getRestTemplate().exchange(path, HttpMethod.DELETE, null, String.class);
        logger.info("Finished deleting app");
    }

    public void waitForRestart(String lastRestartTimestamp) {
        logger.info("Waiting for MarkLogic to restart, last restart timestamp: " + lastRestartTimestamp);
        logger.info("Ignore any HTTP client logging about socket exceptions and retries, those are expected while waiting for MarkLogic to restart");
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
