package com.marklogic.mgmt.admin;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.util.SimplePropertySource;
import com.marklogic.rest.util.RestTemplateUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	void cloudApiKeyAndBasePath() {
		AdminConfig config = configure(
			"mlCloudApiKey", "my-key",
			"mlAdminAuthentication", "cloud",
			"mlAdminBasePath", "/admin/path",
			"mlAdminPort", "8001",
			"mlAdminScheme", "http"
		);

		assertEquals("my-key", config.getCloudApiKey());
		assertEquals("/admin/path", config.getBasePath());
		assertEquals(443, config.getPort(), "When a cloud API key is provided, https and 443 should be assumed");
		assertEquals("https", config.getScheme());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.MarkLogicCloudAuthContext);
		assertEquals("my-key", ((DatabaseClientFactory.MarkLogicCloudAuthContext)bean.getSecurityContext()).getKey());
	}

	@Test
	void certificateAuth() {
		AdminConfig config = configure(
			"mlAdminAuthentication", "certificate",
			"mlAdminCertFile", "my-file.crt",
			"mlAdminCertPassword", "passwd"
		);

		assertEquals("certificate", config.getSecurityContextType());
		assertEquals("my-file.crt", config.getCertFile());
		assertEquals("passwd", config.getCertPassword());
	}

	@Test
	void kerberosAuth() {
		AdminConfig config = configure(
			"mlAdminAuthentication", "kerberos",
			"mlAdminExternalName", "my-name"
		);

		assertEquals("kerberos", config.getSecurityContextType());
		assertEquals("my-name", config.getExternalName());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.KerberosAuthContext);
		assertEquals("my-name", ((DatabaseClientFactory.KerberosAuthContext)bean.getSecurityContext()).getKrbOptions().get("principal"));
	}

	@Test
	void samlAuth() {
		AdminConfig config = configure(
			"mlAdminAuthentication", "saml",
			"mlAdminSamlToken", "my-token"
		);

		assertEquals("saml", config.getSecurityContextType());
		assertEquals("my-token", config.getSamlToken());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.SAMLAuthContext);
		assertEquals("my-token", ((DatabaseClientFactory.SAMLAuthContext)bean.getSecurityContext()).getToken());
	}

	@Test
	void sslHostnameVerifier() {
		AdminConfig config = configure("mlAdminSslHostnameVerifier", "common");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.COMMON, config.getSslHostnameVerifier());

		config = configure("mlAdminSslHostnameVerifier", "ANY");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.ANY, config.getSslHostnameVerifier());

		config = configure("mlAdminSslHostnameVerifier", "strICT");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.STRICT, config.getSslHostnameVerifier());

		assertThrows(IllegalArgumentException.class, () -> configure("mlAdminSslHostnameVerifier", "bogus"));
	}

	private AdminConfig configure(String... properties) {
		return new DefaultAdminConfigFactory(new SimplePropertySource(properties)).newAdminConfig();
	}
}
