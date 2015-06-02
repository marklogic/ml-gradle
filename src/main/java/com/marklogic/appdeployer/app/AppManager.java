package com.marklogic.appdeployer.app;

import com.marklogic.appdeployer.AppConfig;

/**
 * Interface for managing an app - i.e. creating it and deleting it based on the given configuration.
 */
public interface AppManager {

    public void createApp(AppConfig appConfig, ConfigDir configDir);

    public void deleteApp(AppConfig appConfig, ConfigDir configDir);
}
