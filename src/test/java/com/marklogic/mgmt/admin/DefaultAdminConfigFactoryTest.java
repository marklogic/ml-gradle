package com.marklogic.mgmt.admin;

import com.marklogic.mgmt.util.SimplePropertySource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DefaultAdminConfigFactoryTest  {

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

	@Test
	public void sslProperties() {
		AdminConfig config = configure(
			"mlAdminSimpleSsl", "true",
			"mlAdminSslProtocol", "TLSv1.2",
			"mlAdminUseDefaultKeystore", "true",
			"mlAdminTrustManagementAlgorithm", "PKIX"
		);

		assertTrue(config.isConfigureSimpleSsl());
		assertEquals("TLSv1.2", config.getSslProtocol());
		assertTrue(config.isUseDefaultKeystore());
		assertEquals("PKIX", config.getTrustManagementAlgorithm());
	}

	private AdminConfig configure(String... properties) {
		return new DefaultAdminConfigFactory(new SimplePropertySource(properties)).newAdminConfig();
	}
}
