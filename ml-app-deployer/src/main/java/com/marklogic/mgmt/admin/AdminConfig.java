/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.admin;

import com.marklogic.rest.util.RestConfig;


/**
 * Defines the configuration data for talking to the Admin Manage API that is by default on port 8001.
 */
public class AdminConfig extends RestConfig {

	/**
	 * Assumes the usage of "localhost" and 8001 as the host and port.
	 */
    public AdminConfig() {
        super("localhost", 8001, null, null);
    }

    public AdminConfig(String host, String password) {
        super(host, 8001, null, password);
    }

    public AdminConfig(String host, int port, String username, String password) {
        super(host, port, username, password);
    }

	/**
	 * Because this class adds no state and only sets sensible default values for connecting to MarkLogic's Admin app
	 * server, this copy constructor only requires an instance of {@code RestConfig}.
	 *
	 * @param other
	 */
    public AdminConfig(RestConfig other) {
    	super(other);
	}
}
