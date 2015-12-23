package com.marklogic.appdeployer.util;

import java.io.File;
import java.util.List;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.modulesloader.ModulesFinder;
import com.marklogic.client.modulesloader.ModulesLoader;
import com.marklogic.client.modulesloader.impl.DefaultModulesFinder;
import com.marklogic.client.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.modulesloader.impl.XccAssetLoader;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.mgmt.util.SystemPropertySource;

/**
 * This is a hacked together prototype of loading modules from within groovysh. The idea is that all the necessary
 * configuration for loading modules can be collected from system properties, which can be set by a tool like ml-gradle.
 * This class can then be created and started in the startup script for groovysh so that when the shell starts, this
 * class can load new/modified modules.
 */
public class ModulesWatcher extends LoggingObject implements Runnable {

    private long sleepTime = 1000;

    private AppConfig appConfig;

    public ModulesWatcher(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public static void startFromSystemProps() {
        ModulesWatcher mw = new ModulesWatcher(new DefaultAppConfigFactory(new SystemPropertySource()).newAppConfig());
        new Thread(mw).start();
    }

    @Override
    public void run() {
        XccAssetLoader xal = appConfig.newXccAssetLoader();
        ModulesLoader loader = new DefaultModulesLoader(xal);
        DatabaseClient client = appConfig.newDatabaseClient();
        List<String> paths = appConfig.getModulePaths();
        ModulesFinder finder = new DefaultModulesFinder();
        while (true) {
            for (String path : paths) {
                loader.loadModules(new File(path), finder, client);
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ie) {
                // Ignore
            }
        }
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

}
