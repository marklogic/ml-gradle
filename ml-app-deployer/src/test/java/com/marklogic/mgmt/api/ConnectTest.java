/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api;

import org.junit.jupiter.api.Test;

public class ConnectTest extends AbstractApiTest {

	/**
	 * Just smoke testing that we can use the connect method to connect to the same host as before.
	 */
	@Test
	public void test() {
		api.connect(manageConfig.getHost(), manageConfig);
		api.getDb().list();

		api.connect(manageConfig.getHost());
		api.getDb().list();
	}
}
