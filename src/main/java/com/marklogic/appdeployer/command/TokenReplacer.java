package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;

/**
 * Used by plugins to replace tokens in configuration files that are dependent on names of resources in the application.
 * Typically, the tokens are replaced by values in the AppConfig instance. This allows for configuration files to be
 * reused across applications with different names.
 */
public interface TokenReplacer {

    public String replaceTokens(String payload, AppConfig appConfig, boolean isTestResource);
}
