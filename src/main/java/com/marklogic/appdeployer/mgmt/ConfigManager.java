package com.marklogic.appdeployer.mgmt;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.clientutil.LoggingObject;

public class ConfigManager extends LoggingObject {

    private ConfigDir configDir;
    private ManageClient client;

    public ConfigManager(ConfigDir configDir, ManageClient client) {
        this.configDir = configDir;
        this.client = client;
    }

    /**
     * Expect a single JSON/XML file in /rest-apis?
     */
    public void createRestApi(AppConfig config) {
        File f = configDir.getRestApiFile();
        String json = null;
        try {
            json = new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
        new ServiceManager(client).createRestApi(config.getName(), json);
    }
}
