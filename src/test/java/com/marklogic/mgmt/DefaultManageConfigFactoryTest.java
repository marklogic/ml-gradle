package com.marklogic.mgmt;

import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

public class DefaultManageConfigFactoryTest extends Assert {

	@Test
	public void mlUsername() {
		ManageConfig config = configure(
			"mlUsername", "jane",
			"mlPassword", "janepwd"
		);
		assertEquals("jane", config.getUsername());
		assertEquals("jane", config.getSecurityUsername());
		assertEquals("janepwd", config.getPassword());
		assertEquals("janepwd", config.getSecurityPassword());
	}

	/**
	 * This is preserving behavior prior to 3.6.0, but it doesn't seem correct - seems like mlManageUsername should
	 * set securityUsername as opposed to mlUsername.
	 */
	@Test
	public void mlManageUsername() {
		ManageConfig config = configure(
			"mlUsername", "jane",
			"mlPassword", "janepwd",
			"mlManageUsername", "sally",
			"mlManagePassword", "sallypwd"
		);
		assertEquals("sally", config.getUsername());
		assertEquals("jane", config.getSecurityUsername());
		assertEquals("sallypwd", config.getPassword());
		assertEquals("janepwd", config.getSecurityPassword());
	}

	@Test
	public void mlSecurityUsername() {
		ManageConfig config = configure(
			"mlUsername", "jane",
			"mlPassword", "janepwd",
			"mlManageUsername", "sally",
			"mlManagePassword", "sallypwd",
			"mlSecurityUsername", "bob",
			"mlSecurityPassword", "bobpwd"
		);

		assertEquals("sally", config.getUsername());
		assertEquals("bob", config.getSecurityUsername());
		assertEquals("sallypwd", config.getPassword());
		assertEquals("bobpwd", config.getSecurityPassword());
	}

	@Test
	public void mlAdminUsername() {
		ManageConfig config = configure(
			"mlUsername", "jane",
			"mlPassword", "janepwd",
			"mlManageUsername", "sally",
			"mlManagePassword", "sallypwd",
			"mlAdminUsername", "bob",
			"mlAdminPassword", "bobpwd"
		);

		assertEquals("sally", config.getUsername());
		assertEquals("bob", config.getSecurityUsername());
		assertEquals("sallypwd", config.getPassword());
		assertEquals("bobpwd", config.getSecurityPassword());
	}

	@Test
	public void mlHost() {
		ManageConfig config = configure("mlHost", "host1");
		assertEquals("host1", config.getHost());
	}

	@Test
	public void mlManageHost() {
		ManageConfig config = configure("mlHost", "host1", "mlManageHost", "host2");
		assertEquals("host2", config.getHost());
	}


	private ManageConfig configure(String... properties) {
		return new DefaultManageConfigFactory(new SimplePropertySource(properties)).newManageConfig();
	}
}
