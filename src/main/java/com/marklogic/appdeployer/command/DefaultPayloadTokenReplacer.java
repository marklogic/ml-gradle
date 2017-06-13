package com.marklogic.appdeployer.command;

import java.util.Map;

import com.marklogic.appdeployer.AppConfig;

public class DefaultPayloadTokenReplacer implements PayloadTokenReplacer {

    public String replaceTokens(String payload, AppConfig appConfig, boolean isTestResource) {
        payload = replaceDefaultTokens(payload, appConfig, isTestResource);
        return replaceCustomTokens(payload, appConfig, isTestResource);
    }

    protected String replaceDefaultTokens(String payload, AppConfig appConfig, boolean isTestResource) {
        payload = payload.replace("%%NAME%%",
                isTestResource ? appConfig.getTestRestServerName() : appConfig.getRestServerName());
        payload = payload.replace("%%GROUP%%", appConfig.getGroupName());
        payload = payload.replace("%%DATABASE%%",
                isTestResource ? appConfig.getTestContentDatabaseName() : appConfig.getContentDatabaseName());
        payload = payload.replace("%%MODULES_DATABASE%%", appConfig.getModulesDatabaseName());
        payload = payload.replace("%%TRIGGERS_DATABASE%%", appConfig.getTriggersDatabaseName());
        payload = payload.replace("%%SCHEMAS_DATABASE%%", appConfig.getSchemasDatabaseName());
        payload = payload.replace("%%PORT%%", isTestResource ? appConfig.getTestRestPort().toString() : appConfig
                .getRestPort().toString());
        return payload;
    }

    protected String replaceCustomTokens(String payload, AppConfig appConfig, boolean isTestResource) {
        Map<String, String> customTokens = appConfig.getCustomTokens();
        if (customTokens != null) {
            for (String key : customTokens.keySet()) {
                payload = payload.replace(key, customTokens.get(key));
            }
        }
        return payload;
    }
}
