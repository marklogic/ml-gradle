/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.server;

import com.marklogic.mgmt.api.API;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xdbc-server-properties")
public class XdbcServer extends Server {

	public XdbcServer() {
	}

	public XdbcServer(API api, String serverName) {
		super(api, serverName);
	}
}
