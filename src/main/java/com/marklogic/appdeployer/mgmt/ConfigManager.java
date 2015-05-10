package com.marklogic.appdeployer.mgmt;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AppConfig;

public class ConfigManager extends GenericManager {

    private ConfigDir configDir;

    public ConfigManager(ConfigDir configDir, RestTemplate restTemplate, String baseUri) {
        super(restTemplate, baseUri);
        this.configDir = configDir;
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
        new ServiceManager(getRestTemplate(), getBaseUrl()).createRestApi(config.getName(), json);
    }
}
