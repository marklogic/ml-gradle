/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.spring;

import com.marklogic.client.ext.DatabaseClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Extends DatabaseClientConfig to apply 4 Value annotations for host/port/username/password.
 */
@Component
public class SpringDatabaseClientConfig extends DatabaseClientConfig {

	@Autowired
	public SpringDatabaseClientConfig(
		@Value("${marklogic.host}") String host,
		@Value("${marklogic.port}") int port,
		@Value("${marklogic.username}") String username,
		@Value("${marklogic.password}") String password) {
		super(host, port, username, password);
	}

}
