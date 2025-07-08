/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.server;

import com.marklogic.mgmt.api.API;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "http-server-properties")
public class HttpServer extends Server {

	public HttpServer() {
	}

	public HttpServer(API api, String serverName) {
		super(api, serverName);
	}
}
