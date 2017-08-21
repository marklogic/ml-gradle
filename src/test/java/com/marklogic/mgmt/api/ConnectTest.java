package com.marklogic.mgmt.api;

import org.junit.Test;

public class ConnectTest extends AbstractApiTest {

	/**
	 * Just smoke testing that we can use the connect method to connect to the same host as before.
	 */
	@Test
	public void test() {
		manageConfig.setScheme("https");
		manageConfig.setConfigureSimpleSsl(true);

		api.connect(manageConfig.getHost(), manageConfig);
		api.getDb().list();

		api.connect(manageConfig.getHost());
		api.getDb().list();
	}
}
