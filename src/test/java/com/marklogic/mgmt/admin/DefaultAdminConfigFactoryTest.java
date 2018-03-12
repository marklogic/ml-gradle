package com.marklogic.mgmt.admin;

import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

public class DefaultAdminConfigFactoryTest extends Assert {

	@Test
	public void mlUsername() {
		AdminConfig config = configure(
			"mlUsername", "jane",
			"mlPassword", "janepwd"
		);
		assertEquals("jane", config.getUsername());
		assertEquals("janepwd", config.getPassword());
	}

	@Test
	public void mlManageUsername() {
		AdminConfig config = configure(
			"mlUsername", "jane",
			"mlPassword", "janepwd",
			"mlManageUsername", "sally",
			"mlManagePassword", "sallypwd"
		);
		assertEquals("sally", config.getUsername());
		assertEquals("sallypwd", config.getPassword());
	}

	@Test
	public void mlHost() {
		AdminConfig config = configure("mlHost", "host1");
		assertEquals("host1", config.getHost());
	}

	@Test
	public void mlManageHost() {
		AdminConfig config = configure("mlHost", "host1", "mlAdminHost", "host2");
		assertEquals("host2", config.getHost());
	}

	private AdminConfig configure(String... properties) {
		return new DefaultAdminConfigFactory(new SimplePropertySource(properties)).newAdminConfig();
	}
}
