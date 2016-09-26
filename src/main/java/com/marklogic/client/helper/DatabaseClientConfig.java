package com.marklogic.client.helper;

import javax.net.ssl.SSLContext;

import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;

public class DatabaseClientConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;
    private Authentication authentication = Authentication.DIGEST;
    private SSLContext sslContext;
    private SSLHostnameVerifier sslHostnameVerifier;
    
    public DatabaseClientConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
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

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
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

    public SSLContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public SSLHostnameVerifier getSslHostnameVerifier() {
        return sslHostnameVerifier;
    }

    public void setSslHostnameVerifier(SSLHostnameVerifier sslHostnameVerifier) {
        this.sslHostnameVerifier = sslHostnameVerifier;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

}
