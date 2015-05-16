package com.marklogic.appdeployer.mgmt;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.services.ServiceManager;
import com.marklogic.clientutil.LoggingObject;

/**
 * This is an "uber" manager that provides high-level methods that depend on NounManager classes for getting things
 * done.
 */
public class ConfigManager extends LoggingObject {

    private ManageClient client;

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

    public void deleteRestApi(AppConfig config, boolean includeModules, boolean includeContent) {
        String path = client.getBaseUrl() + "/v1/rest-apis/" + config.getName() + "?";
        if (includeModules) {
            path += "include=modules&";
        }
        if (includeContent) {
            path += "include=content";
        }
        logger.info("Deleting app, path: " + path);
        client.getRestTemplate().delete(path);
        logger.info("Finished deleting app");
    }

    protected String copyFileToString(File f) {
        try {
            return new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
    }
}
