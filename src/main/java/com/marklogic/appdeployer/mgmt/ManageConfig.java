package com.marklogic.appdeployer.mgmt;

public class ManageConfig {

    private String host = "localhost";
    private int port = 8002;
    private String username = "admin";
    private String password = "admin";

    public ManageConfig() {
        super();
    }

    public ManageConfig(String host, int port, String username, String password) {
        super();
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getBaseUrl() {
        return String.format("http://%s:%d", host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
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

}
