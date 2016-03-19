package com.marklogic.client.modulesloader.impl;

import java.io.File;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * Debug program to testing module loading.
 */
public class LoadModulesDebug {

    public static void main(String[] args) throws Exception {
        // if (true) {
        // File dir = new File("c:/temp/modules");
        // for (int i = 0; i < 500; i++) {
        // FileCopyUtils.copy(new String("Hello " + System.currentTimeMillis()).getBytes(),
        // new File(dir, i + ".xqy"));
        // }
        // return;
        // }
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "admin", "admin",
                Authentication.DIGEST);

        DatabaseClient modulesClient = DatabaseClientFactory.newClient("localhost", 8000, "Modules", "admin", "admin",
                Authentication.DIGEST);

        RestApiAssetLoader raal = new RestApiAssetLoader(modulesClient);
        XccAssetLoader xal = new XccAssetLoader();
        xal.setHost("localhost");
        xal.setDatabaseName("Modules");
        xal.setPassword("admin");
        xal.setUsername("admin");
        xal.setPort(8000);

        DefaultModulesLoader l = new DefaultModulesLoader();
        l.setModulesManager(null);
        l.setRestApiAssetLoader(raal);
        l.setXccAssetLoader(xal);

        String path = "c:/temp/modules";
        try {
            l.loadModules(new File(path), new DefaultModulesFinder(), client);
        } finally {
            client.release();
            modulesClient.release();
        }
    }
}
