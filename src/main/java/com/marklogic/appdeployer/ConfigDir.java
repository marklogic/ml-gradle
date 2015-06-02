package com.marklogic.appdeployer;

import java.io.File;

/**
 * Defines all of the directories where configuration files can be found. This is decoupled from the NounManager
 * classes, who don't need to care where to look for configuration files, they just need to care about how to load the
 * data in those files.
 * 
 * Every directory path referenced in this should have a setter so that it can be modified in e.g. a Gradle build file.
 */
public class ConfigDir {

    private File baseDir;

    private String databasesPath = "databases";
    private String restApiPath = "rest-api.json";

    public ConfigDir() {
        this(new File("src/main/ml-config"));
    }

    public ConfigDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public File getContentDatabaseFile() {
        File dir = new File(baseDir, databasesPath);
        return new File(dir, "content-database.json");
    }

    public File getTriggersDatabaseFile() {
        return new File(new File(baseDir, databasesPath), "triggers-database.json");
    }

    public File getRestApiFile() {
        return new File(baseDir, restApiPath);
    }

    public void setDatabasesPath(String databasesPath) {
        this.databasesPath = databasesPath;
    }

    public void setRestApiPath(String restApiPath) {
        this.restApiPath = restApiPath;
    }

    public File getBaseDir() {
        return baseDir;
    }
}
