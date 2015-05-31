package com.marklogic.appdeployer;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates configuration for both managing an app and loading modules into an app.
 */
public class AppConfig {

    private String name;
    private String host = "localhost";
    private String username = "admin";
    private String password = "admin";

    private Integer restPort;
    private Integer xdbcPort;
    private Integer testRestPort;
    private Integer testXdbcPort;
    private Integer modulesXdbcPort;

    private String defaultModulePath;
    private List<String> modulePaths;

    private String groupName = "Default";
    private String contentDatabaseFilePath;
    private String httpServerFilePath;
    private String triggersDatabaseFilePath;
    private String schemasDatabaseFilePath;
    private String xdbcServerFilePath;

    public AppConfig() {
        this("src/main/xqy");
    }

    public AppConfig(String defaultModulePath) {
        this.defaultModulePath = defaultModulePath;

        modulePaths = new ArrayList<String>();
        modulePaths.add(defaultModulePath);
        contentDatabaseFilePath = defaultModulePath + "/packages/content-database.xml";
        httpServerFilePath = defaultModulePath + "/packages/http-server.xml";
        triggersDatabaseFilePath = defaultModulePath + "/packages/triggers-database.xml";
        schemasDatabaseFilePath = defaultModulePath + "/packages/schemas-database.xml";
        xdbcServerFilePath = defaultModulePath + "/packages/xdbc-server.xml";
    }

    public boolean isTestPortSet() {
        return testRestPort != null && testRestPort > 0;
    }

    public String getPackageName() {
        return name + "-package";
    }

    public String getRestServerName() {
        return name;
    }

    public String getTestRestServerName() {
        return name + "-test";
    }

    public String getXdbcServerName() {
        return getContentDatabaseName() + "-xdbc";
    }

    public String getTestXdbcServerName() {
        return getTestContentDatabaseName() + "-xdbc";
    }

    public String getModulesXdbcServerName() {
        return name + "-modules-xdbc";
    }

    public String getContentDatabaseName() {
        return name + "-content";
    }

    public String getTestContentDatabaseName() {
        return name + "-test-content";
    }

    public String getModulesDatabaseName() {
        return name + "-modules";
    }

    public String getTriggersDatabaseName() {
        return name + "-triggers";
    }

    public String getSchemasDatabaseName() {
        return name + "-schemas";
    }

    public String getXccUrl() {
        return String.format("xcc://%s:%s@%s:%d", username, password, host, xdbcPort);
    }

    public String getTestXccUrl() {
        return String.format("xcc://%s:%s@%s:%d", username, password, host, testXdbcPort);
    }

    public String getModulesXccUrl() {
        return String.format("xcc://%s:%s@%s:%d", username, password, host, modulesXdbcPort);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRestPort() {
        return restPort;
    }

    public void setRestPort(Integer restPort) {
        this.restPort = restPort;
    }

    public Integer getXdbcPort() {
        return xdbcPort;
    }

    public void setXdbcPort(Integer xdbcPort) {
        this.xdbcPort = xdbcPort;
    }

    public Integer getTestRestPort() {
        return testRestPort;
    }

    public void setTestRestPort(Integer testRestPort) {
        this.testRestPort = testRestPort;
    }

    public Integer getTestXdbcPort() {
        return testXdbcPort;
    }

    public void setTestXdbcPort(Integer testXdbcPort) {
        this.testXdbcPort = testXdbcPort;
    }

    public Integer getModulesXdbcPort() {
        return modulesXdbcPort;
    }

    public void setModulesXdbcPort(Integer modulesXdbcPort) {
        this.modulesXdbcPort = modulesXdbcPort;
    }

    public String getDefaultModulePath() {
        return defaultModulePath;
    }

    public void setDefaultModulePath(String defaultModulePath) {
        this.defaultModulePath = defaultModulePath;
    }

    public List<String> getModulePaths() {
        return modulePaths;
    }

    public void setModulePaths(List<String> modulePaths) {
        this.modulePaths = modulePaths;
    }

    public String getContentDatabaseFilePath() {
        return contentDatabaseFilePath;
    }

    public void setContentDatabaseFilePath(String contentDatabaseFilePath) {
        this.contentDatabaseFilePath = contentDatabaseFilePath;
    }

    public String getHttpServerFilePath() {
        return httpServerFilePath;
    }

    public void setHttpServerFilePath(String httpServerFilePath) {
        this.httpServerFilePath = httpServerFilePath;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTriggersDatabaseFilePath() {
        return triggersDatabaseFilePath;
    }

    public void setTriggersDatabaseFilePath(String triggersDatabaseFilePath) {
        this.triggersDatabaseFilePath = triggersDatabaseFilePath;
    }

    public String getSchemasDatabaseFilePath() {
        return schemasDatabaseFilePath;
    }

    public void setSchemasDatabaseFilePath(String schemasDatabaseFilePath) {
        this.schemasDatabaseFilePath = schemasDatabaseFilePath;
    }

    public String getXdbcServerFilePath() {
        return xdbcServerFilePath;
    }

    public void setXdbcServerFilePath(String xdbcServerFilePath) {
        this.xdbcServerFilePath = xdbcServerFilePath;
    }

}
