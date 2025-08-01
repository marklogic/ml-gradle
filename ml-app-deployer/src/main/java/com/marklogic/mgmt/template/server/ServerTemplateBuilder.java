/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.template.server;

import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

public class ServerTemplateBuilder extends GenericTemplateBuilder {

	public ServerTemplateBuilder() {
		super(Server.class);
		addDefaultPropertyValue("server-name", "CHANGEME-name-of-server");
		addDefaultPropertyValue("group-name", "Default");
		addDefaultPropertyValue("server-type", "http");
		addDefaultPropertyValue("root", "/");
		addDefaultPropertyValue("enabled", "true");
		addDefaultPropertyValue("port", "8099");
		addDefaultPropertyValue("modules-database", "Modules");
		addDefaultPropertyValue("content-database", "Documents");
		addDefaultPropertyValue("authentication", "digest");
	}
}
