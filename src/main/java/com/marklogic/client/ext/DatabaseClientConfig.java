package com.marklogic.client.ext;

import javax.net.ssl.SSLContext;

import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;

/**
 * Captures all the possible inputs used to construct an instance of DatabaseClient.
 *
 * The Authentication attribute is deprecated, as this class is deprecated in version 4.x of the Java Client API.
 * Use securityContextType and the constants defined in this class to define the particular kind of security context
 * that should be constructed.
 */
public class DatabaseClientConfig {

	private SecurityContextType securityContextType = SecurityContextType.DIGEST;
	private String host;
    private int port;
    private String username;
    private String password;
    private String database;
    private SSLContext sslContext;
    private SSLHostnameVerifier sslHostnameVerifier;
    private String certFile;
    private String certPassword;
    private String externalName;

	@Deprecated
	private Authentication authentication;

	public DatabaseClientConfig() {
    }

    public DatabaseClientConfig(String host, int port) {
    	this.host = host;
    	this.port = port;
    }

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

    @Deprecated
    public Authentication getAuthentication() {
        return authentication;
    }

    @Deprecated
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

	public SecurityContextType getSecurityContextType() {
		return securityContextType;
	}

	public void setSecurityContextType(SecurityContextType securityContextType) {
		this.securityContextType = securityContextType;
	}

	public String getCertFile() {
		return certFile;
	}

	public void setCertFile(String certFile) {
		this.certFile = certFile;
	}

	public String getCertPassword() {
		return certPassword;
	}

	public void setCertPassword(String certPassword) {
		this.certPassword = certPassword;
	}

	public String getExternalName() {
		return externalName;
	}

	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}
}
