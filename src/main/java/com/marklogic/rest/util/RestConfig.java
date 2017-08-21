package com.marklogic.rest.util;

import java.net.URI;
import java.net.URISyntaxException;

public class RestConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private String scheme = "http";
    private boolean configureSimpleSsl;

    public RestConfig() {
    }

    public RestConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("[scheme: %s, host: %s, port: %d, username: %s]", scheme, host, port, username);
    }

    /**
     * Using the java.net.URI constructor that takes a string. Using any other constructor runs into encoding problems,
     * e.g. when a mimetype has a plus in it, that plus needs to be encoded, but doing as %2B will result in the % being
     * double encoded. Unfortunately, it seems some encoding is still needed - e.g. for a pipeline like "Flexible Replication"
     * with a space in its name, the space must be encoded properly as a "+".
     *
     * @param path
     * @return
     */
    public URI buildUri(String path) {
        try {
            return new URI(String.format("%s://%s:%d%s", getScheme(), getHost(), getPort(), path.replace(" ", "+")));
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Unable to build URI for path: " + path + "; cause: " + ex.getMessage(), ex);
        }
    }

    public String getBaseUrl() {
        return String.format("%s://%s:%d", scheme, host, port);
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

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

	public boolean isConfigureSimpleSsl() {
		return configureSimpleSsl;
	}

	public void setConfigureSimpleSsl(boolean configureSimpleSsl) {
		this.configureSimpleSsl = configureSimpleSsl;
	}
}
