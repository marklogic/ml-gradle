package com.marklogic.appdeployer.project;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.clientutil.LoggingObject;

public abstract class AbstractPlugin extends LoggingObject {

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }

    protected String copyFileToString(File f) {
        try {
            return new String(FileCopyUtils.copyToByteArray(f));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
    }

    protected String replaceConfigTokens(String payload, AppConfig config, boolean isTestResource) {
        payload = payload.replace("%%NAME%%",
                isTestResource ? config.getTestRestServerName() : config.getRestServerName());
        payload = payload.replace("%%GROUP%%", config.getGroupName());
        payload = payload.replace("%%DATABASE%%",
                isTestResource ? config.getTestContentDatabaseName() : config.getContentDatabaseName());
        payload = payload.replace("%%MODULES-DATABASE%%", config.getModulesDatabaseName());
        payload = payload.replace("%%TRIGGERS_DATABASE%%", config.getTriggersDatabaseName());
        payload = payload.replace("%%PORT%%", isTestResource ? config.getTestRestPort().toString() : config
                .getRestPort().toString());
        return payload;
    }

}
