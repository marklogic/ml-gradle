package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;

/**
 * TODO Eventually want a way to be able to e.g. inject Gradle properties into there so it's easy to customize.
 */
public class DefaultTokenReplacer implements TokenReplacer {

    public String replaceTokens(String payload, AppConfig appConfig, boolean isTestResource) {
        payload = payload.replace("%%NAME%%",
                isTestResource ? appConfig.getTestRestServerName() : appConfig.getRestServerName());
        payload = payload.replace("%%GROUP%%", appConfig.getGroupName());
        payload = payload.replace("%%DATABASE%%",
                isTestResource ? appConfig.getTestContentDatabaseName() : appConfig.getContentDatabaseName());
        payload = payload.replace("%%MODULES_DATABASE%%", appConfig.getModulesDatabaseName());
        payload = payload.replace("%%TRIGGERS_DATABASE%%", appConfig.getTriggersDatabaseName());
        payload = payload.replace("%%PORT%%", isTestResource ? appConfig.getTestRestPort().toString() : appConfig
                .getRestPort().toString());
        return payload;
    }

}
