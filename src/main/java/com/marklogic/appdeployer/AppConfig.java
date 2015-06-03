package com.marklogic.appdeployer;

import java.util.ArrayList;
import java.util.List;

import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * Encapsulates the connection information for an application and where its modules can be found. Should possibly add
 * ConfigDir to this.
 */
public class AppConfig {

    private String name;
    private String host = "localhost";

    // User/password for authenticating against the REST API
    private String username = "admin";
    private String password = "admin";
    private Authentication authentication = Authentication.DIGEST;

    private Integer restPort;
    private Integer xdbcPort;
    private Integer testRestPort;
    private Integer testXdbcPort;
    private Integer modulesXdbcPort;

    private List<String> modulePaths;

    private String groupName = "Default";

    public AppConfig() {
        this("src/main/ml-modules");
    }

    public AppConfig(String defaultModulePath) {
        modulePaths = new ArrayList<String>();
        modulePaths.add(defaultModulePath);
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

    public List<String> getModulePaths() {
        return modulePaths;
    }

    public void setModulePaths(List<String> modulePaths) {
        this.modulePaths = modulePaths;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

}
