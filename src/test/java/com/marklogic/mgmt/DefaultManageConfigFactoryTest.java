package com.marklogic.mgmt;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultManageConfigFactoryTest  {

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
	public void sslProperties() {
		ManageConfig config = configure(
			"mlManageSimpleSsl", "true",
			"mlManageSslProtocol", "TLSv1.2",
			"mlManageUseDefaultKeystore", "true",
			"mlManageTrustManagementAlgorithm", "PKIX"
		);

		assertTrue(config.isConfigureSimpleSsl());
		assertEquals("TLSv1.2", config.getSslProtocol());
		assertTrue(config.isUseDefaultKeystore());
		assertEquals("PKIX", config.getTrustManagementAlgorithm());
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

	@Test
	void cloudApiKeyAndBasePath() {
		ManageConfig config = configure(
			"mlCloudApiKey", "my-key",
			"mlManageAuthentication", "cloud",
			"mlManageBasePath", "/manage/path",
			"mlManagePort", "8002",
			"mlManageScheme", "http"
		);

		assertEquals("my-key", config.getCloudApiKey());
		assertEquals("/manage/path", config.getBasePath());
		assertEquals(443, config.getPort(), "When a cloud API key is provided, the mlManagePort and mlManageScheme " +
			"options should be overridden since https/443 are guaranteed to be the correct values");
		assertEquals("https", config.getScheme());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.MarkLogicCloudAuthContext);
		assertEquals("my-key", ((DatabaseClientFactory.MarkLogicCloudAuthContext)bean.getSecurityContext()).getKey());
	}

	@Test
	void certificateAuth() {
		ManageConfig config = configure(
			"mlManageAuthentication", "certificate",
			"mlManageCertFile", "my-file.crt",
			"mlManageCertPassword", "passwd"
		);

		assertEquals("certificate", config.getSecurityContextType());
		assertEquals("my-file.crt", config.getCertFile());
		assertEquals("passwd", config.getCertPassword());
	}

	@Test
	void kerberosAuth() {
		ManageConfig config = configure(
			"mlManageAuthentication", "kerberos",
			"mlManageExternalName", "my-name"
		);

		assertEquals("kerberos", config.getSecurityContextType());
		assertEquals("my-name", config.getExternalName());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.KerberosAuthContext);
		assertEquals("my-name", ((DatabaseClientFactory.KerberosAuthContext)bean.getSecurityContext()).getKrbOptions().get("principal"));
	}

	@Test
	void samlAuth() {
		ManageConfig config = configure(
			"mlManageAuthentication", "saml",
			"mlManageSamlToken", "my-token"
		);

		assertEquals("saml", config.getSecurityContextType());
		assertEquals("my-token", config.getSamlToken());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.SAMLAuthContext);
		assertEquals("my-token", ((DatabaseClientFactory.SAMLAuthContext)bean.getSecurityContext()).getToken());
	}

	private ManageConfig configure(String... properties) {
		return new DefaultManageConfigFactory(new SimplePropertySource(properties)).newManageConfig();
	}
}
