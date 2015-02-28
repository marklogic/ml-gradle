package com.marklogic.appdeployer;

import java.util.ArrayList;
import java.util.List;

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

    private String defaultModulePath = "src/main/xqy";
    private List<String> modulePaths;

    public AppConfig() {
        modulePaths = new ArrayList<String>();
        modulePaths.add(defaultModulePath);
    }

    public String getXccUrl() {
        return "xcc://${username}:${password}@${host}:${xdbcPort}";
    }

    public String getTestXccUrl() {
        return "xcc://${username}:${password}@${host}:${testXdbcPort}";
    }

    public String getModulesXccUrl() {
        return "xcc://${username}:${password}@${host}:${modulesXdbcPort}";
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
}
