package com.marklogic.appdeployer;

public class ManageConfig {

    private String host = "localhost";
    private Integer port = 8002;
    private String username = "admin";
    private String password = "admin";

    private String contentDatabaseFilePath = "src/main/xqy/packages/content-database.xml";
    private String httpServerFilePath = "src/main/xqy/packages/http-server.xml";

    public String getUri() {
        return String.format("http://%s:%d", host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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
}
