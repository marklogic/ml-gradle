package com.marklogic.clientutil;

import com.marklogic.client.DatabaseClientFactory.Authentication;

public class DatabaseClientConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private String description;
    private Authentication authentication = Authentication.DIGEST;

    public DatabaseClientConfig(String host, int port, String username, String password) {
        this(host, port, username, password, null);
    }

    public DatabaseClientConfig(String host, int port, String username, String password, String description) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[%s@%s:%d]", username, host, port, username);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
