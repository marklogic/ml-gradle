package com.marklogic.appdeployer.mgmt;

import java.io.File;

import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.util.RestTemplateUtil;

public class TestClient {

    public static void main(String[] args) throws Exception {

        // Define how to connect to the ML manage app
        ManageConfig manageConfig = new ManageConfig("localhost", 8002, "admin", "admin");

        // Configure a Spring RestTemplate instance that can talk to the ML manage app
        RestTemplate restTemplate = RestTemplateUtil.newRestTemplate(manageConfig);

        // Define where configuration files for the ML app are
        ConfigDir configDir = new ConfigDir(new File("src/test/resources/sample-app/src/main/ml-config"));

        // Build a ConfigManager with all the fun method
        ConfigManager configMgr = new ConfigManager(configDir, restTemplate, manageConfig.getBaseUrl());

        // Define app configuration
        AppConfig config = new AppConfig();
        config.setName("sample-app");
        config.setRestPort(8070);

        // Now start calling fun methods that get things done
        configMgr.createRestApi(config);

        System.out.println("All done!");
    }
}
