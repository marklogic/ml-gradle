/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import com.marklogic.rest.util.RestConfig;

import javax.net.ssl.SSLContext;

/**
 * Defines the configuration data for talking to the Mgmt REST API. Also includes properties for the security user, as this
 * user is typically needed for creating an app-specific user (which may depend on app-specific roles and privileges)
 * which is then used for deploying every other resources.
 *
 * If securityUsername and securityPassword are not set, they default to the username/password attribute values.
 * Additionally, as of version 3.8.3, setSecuritySslContext can be called to provide an SSLContext for the connection
 * made using securityUsername and securityPassword.
 */
public class ManageConfig extends RestConfig {

	private String securityUsername;
	private String securityPassword;
	private SSLContext securitySslContext;
	private boolean cleanJsonPayloads = false;

	/**
	 * Assumes the use of "localhost" and 8002 as the host and port.
	 */
	public ManageConfig() {
		this("localhost", null);
	}

	/**
	 * Assumes the use of 8002 as the port.
	 *
	 * @param host
	 * @param password
	 */
	public ManageConfig(String host, String password) {
		super(host, 8002, null, password);
	}

	public ManageConfig(String host, int port, String username, String password) {
		super(host, port, username, password);
		setSecurityUsername(username);
		setSecurityPassword(password);
	}

	public ManageConfig(ManageConfig other) {
		super(other);
		this.securityUsername = other.securityUsername;
		this.securityPassword = other.securityPassword;
		this.securitySslContext = other.securitySslContext;
		this.cleanJsonPayloads = other.cleanJsonPayloads;
	}

	public boolean isCleanJsonPayloads() {
		return cleanJsonPayloads;
	}

	public void setCleanJsonPayloads(boolean cleanJsonPayloads) {
		this.cleanJsonPayloads = cleanJsonPayloads;
	}

	public String getSecurityUsername() {
		return securityUsername;
	}

	public void setSecurityUsername(String securityUsername) {
		this.securityUsername = securityUsername;
	}

	public String getSecurityPassword() {
		return securityPassword;
	}

	public void setSecurityPassword(String securityPassword) {
		this.securityPassword = securityPassword;
	}

	public SSLContext getSecuritySslContext() {
		return securitySslContext;
	}

	public void setSecuritySslContext(SSLContext securitySslContext) {
		this.securitySslContext = securitySslContext;
	}
}
