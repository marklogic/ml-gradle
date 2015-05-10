package com.marklogic.appdeployer.mgmt;

import java.io.File;

public class ConfigDir {

    private File baseDir;

    public ConfigDir() {
        this(new File("src/main/ml-config"));
    }

    public ConfigDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public File[] getDatabaseFiles() {
        return new File(baseDir, "databases").listFiles();
    }

    public File getRestApiFile() {
        return new File(baseDir, "rest-api.json");
    }
}
