package com.marklogic.appdeployer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private String defaultContentDatabaseFilename = "content-database.json";

    private String restApiPath = "rest-api.json";

    private List<File> contentDatabaseFiles;

    public ConfigDir() {
        this(new File("src/main/ml-config"));
    }

    public ConfigDir(File baseDir) {
        setBaseDir(baseDir);
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
        initializeContentDatabaseFiles();
    }

    public File getDatabasesDir() {
        return new File(baseDir, databasesPath);
    }

    protected void initializeContentDatabaseFiles() {
        contentDatabaseFiles = new ArrayList<>();
        contentDatabaseFiles.add(new File(getDatabasesDir(), defaultContentDatabaseFilename));
    }

    public File getRestApiFile() {
        return new File(baseDir, restApiPath);
    }

    public File getRestApiServerFile() {
        return new File(getServersDir(), "rest-api-server.json");
    }

    public File getSecurityDir() {
        return new File(baseDir, "security");
    }

    public File getServersDir() { return new File(baseDir, "servers"); }

    public File getForestsDir() {
        return new File(baseDir, "forests");
    }

    public File getCpfDir() {
        return new File(baseDir, "cpf");
    }

    public File getClustersDir() {
        return new File(baseDir, "clusters");
    }

    public File getAlertDir() {
        return new File(baseDir, "alert");
    }

    public File getFlexrepDir() {
        return new File(baseDir, "flexrep");
    }

	public File getTemporalDir() { return new File(baseDir, "temporal"); }

	public File getTasksDir() {
    	return new File(baseDir, "tasks");
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

    public List<File> getContentDatabaseFiles() {
        return contentDatabaseFiles;
    }

    public void setContentDatabaseFiles(List<File> contentDatabaseFiles) {
        this.contentDatabaseFiles = contentDatabaseFiles;
    }

    public String getDefaultContentDatabaseFilename() {
        return defaultContentDatabaseFilename;
    }

    public void setDefaultContentDatabaseFilename(String contentDatabaseFilename) {
        this.defaultContentDatabaseFilename = contentDatabaseFilename;
    }
}
