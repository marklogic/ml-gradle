package com.marklogic.mgmt;

import com.marklogic.rest.util.RestConfig;

/**
 * Defines the configuration data for talking to the Mgmt REST API. Also includes properties for the admin user, as this
 * user is typically needed for managing resources such as roles and users. If adminUsername and adminPassword are not
 * set, they default to the username/password attribute values.
 */
public class ManageConfig extends RestConfig {

	/**
	 * These are assumed as sensible defaults in a development environment, where teams often use admin/admin for the
	 * admin login. They are of course expected to change in a real environment.
	 */
	public static final String DEFAULT_USERNAME = "admin";
	public static final String DEFAULT_PASSWORD = "admin";

	private boolean cleanJsonPayloads = false;

	public ManageConfig() {
		this("localhost", DEFAULT_PASSWORD);
	}

	public ManageConfig(String host, String password) {
		super(host, 8002, DEFAULT_USERNAME, password);
	}

	public ManageConfig(String host, int port, String username, String password) {
		super(host, port, username, password);
	}

	@Override
	public String toString() {
		return String.format("[ManageConfig host: %s, port: %d, username: %s]", getHost(),
			getPort(), getUsername());
	}

	public boolean isCleanJsonPayloads() {
		return cleanJsonPayloads;
	}

	public void setCleanJsonPayloads(boolean cleanJsonPayloads) {
		this.cleanJsonPayloads = cleanJsonPayloads;
	}
}
