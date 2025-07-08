/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.admin;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultAdminConfigFactoryTest  {

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

		assertEquals("https", config.getScheme());
		assertTrue(config.isConfigureSimpleSsl());
		assertEquals("TLSv1.2", config.getSslProtocol());
		assertTrue(config.isUseDefaultKeystore());
		assertEquals("PKIX", config.getTrustManagementAlgorithm());
	}

	@Test
	void simpleSsl() {
		AdminConfig config = configure(
			"mlAdminSimpleSsl", "true",
			"mlUsername", "admin",
			"mlPassword", "admin"
		);

		assertEquals("https", config.getScheme());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		SSLHostnameVerifier verifier = bean.getSecurityContext().getSSLHostnameVerifier();
		assertEquals(SSLHostnameVerifier.ANY, verifier, "simpleSsl should default to using the ANY hostname verifier");
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
		assertEquals("my-key", ((DatabaseClientFactory.MarkLogicCloudAuthContext)bean.getSecurityContext()).getApiKey());
	}

	@Test
	void certificateAuth() {
		AdminConfig config = configure(
			"mlAdminAuthentication", "certificate",
			"mlAdminCertFile", "my-file.crt",
			"mlAdminCertPassword", "passwd"
		);

		assertEquals("certificate", config.getAuthType());
		assertEquals("my-file.crt", config.getCertFile());
		assertEquals("passwd", config.getCertPassword());
	}

	@Test
	void kerberosAuth() {
		AdminConfig config = configure(
			"mlAdminAuthentication", "kerberos",
			"mlAdminExternalName", "my-name"
		);

		assertEquals("kerberos", config.getAuthType());
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

		assertEquals("saml", config.getAuthType());
		assertEquals("my-token", config.getSamlToken());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.SAMLAuthContext);
		assertEquals("my-token", ((DatabaseClientFactory.SAMLAuthContext)bean.getSecurityContext()).getToken());
	}

	@Test
	void oauth() {
		AdminConfig config = configure(
			"mlAdminAuthentication", "oauth",
			"mlAdminOauthToken", "my-token"
		);

		assertEquals("oauth", config.getAuthType());
		assertEquals("my-token", config.getOauthToken());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.OAuthContext);
		assertEquals("my-token", ((DatabaseClientFactory.OAuthContext)bean.getSecurityContext()).getToken());
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

	@Test
	void mlSslHostnameVerifier() {
		AdminConfig config = configure("mlSslHostnameVerifier", "any");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.ANY, config.getSslHostnameVerifier());

		config = configure(
			"mlSslHostnameVerifier", "any",
			"mlAdminSslHostnameVerifier", "strict"
		);
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.STRICT, config.getSslHostnameVerifier());
	}

	@Test
	void mlAuthentication() {
		AdminConfig config = configure("mlAuthentication", "cloud");
		assertEquals("cloud", config.getAuthType());

		config = configure(
			"mlAuthentication", "cloud",
			"mlAdminAuthentication", "basic"
		);
		assertEquals("basic", config.getAuthType());
	}

	@Test
	void mlAdminBasePath() {
		AdminConfig config = configure(
			"mlAdminBasePath", "/my/custom/admin/path"
		);
		assertEquals("/my/custom/admin/path", config.getBasePath(),
			"If a user only specifies mlAdminBasePath, then the assumption is that they're using a reverse proxy and " +
				"have defined their own custom path for the Admin app server. They could be using ML Cloud, but " +
				"that's not likely as it would make more sense to still define mlCloudBasePath and then set " +
				"mlAdminBasePath to the custom Admin part (as a user is not allowed to setup a base path in ML Cloud " +
				"that doesn't begin with their common base path).");
	}

	@Test
	void mlCloudBasePath() {
		AdminConfig config = configure(
			"mlCloudBasePath", "/my/domain"
		);
		assertEquals("/my/domain/admin", config.getBasePath(),
			"If a user only specifies mlCloudBasePath, then the assumption is that they're good to go with the default " +
				"Admin base path setup in ML Cloud, and so they only need to define the 'cloud base path' that occurs " +
				"before '/admin'");
	}

	@Test
	void mlCloudBasePathWithAdminBasePath() {
		AdminConfig config = configure(
			"mlCloudBasePath", "/my/domain",
			"mlAdminBasePath", "/my-custom-admin-path"
		);
		assertEquals("/my/domain/my-custom-admin-path", config.getBasePath(),
			"If a user specifies both mlCloudBasePath and mlAdminBasePath, then the assumption is that they've " +
				"changed the default Admin base path but it still begins with the common base path defined by " +
				"mlCloudBasePath.");
	}

	@Test
	void keyStore() {
		AdminConfig config = configure(
			"mlAdminKeyStorePath", "/my.jks",
			"mlAdminKeyStorePassword", "abc123",
			"mlAdminKeyStoreType", "JKS",
			"mlAdminKeyStoreAlgorithm", "SunX509"
		);

		assertEquals("/my.jks", config.getKeyStorePath());
		assertEquals("abc123", config.getKeyStorePassword());
		assertEquals("JKS", config.getKeyStoreType());
		assertEquals("SunX509", config.getKeyStoreAlgorithm());
		assertEquals("https", config.getScheme());
	}

	@Test
	void trustStore() {
		AdminConfig config = configure(
			"mlAdminTrustStorePath", "/my.jks",
			"mlAdminTrustStorePassword", "abc123",
			"mlAdminTrustStoreType", "JKS",
			"mlAdminTrustStoreAlgorithm", "SunX509"
		);

		assertEquals("/my.jks", config.getTrustStorePath());
		assertEquals("abc123", config.getTrustStorePassword());
		assertEquals("JKS", config.getTrustStoreType());
		assertEquals("SunX509", config.getTrustStoreAlgorithm());
		assertEquals("https", config.getScheme());
	}

	@Test
	void globalKeyStoreAndTrustStore() {
		AdminConfig config = configure(
			"mlKeyStorePath", "/key.jks",
			"mlKeyStorePassword", "abc",
			"mlKeyStoreType", "JKS1",
			"mlKeyStoreAlgorithm", "SunX5091",
			"mlTrustStorePath", "/trust.jks",
			"mlTrustStorePassword", "123",
			"mlTrustStoreType", "JKS2",
			"mlTrustStoreAlgorithm", "SunX5092"
		);

		assertEquals("/key.jks", config.getKeyStorePath());
		assertEquals("abc", config.getKeyStorePassword());
		assertEquals("JKS1", config.getKeyStoreType());
		assertEquals("SunX5091", config.getKeyStoreAlgorithm());
		assertEquals("/trust.jks", config.getTrustStorePath());
		assertEquals("123", config.getTrustStorePassword());
		assertEquals("JKS2", config.getTrustStoreType());
		assertEquals("SunX5092", config.getTrustStoreAlgorithm());
		assertEquals("https", config.getScheme());
	}

	private AdminConfig configure(String... properties) {
		return new DefaultAdminConfigFactory(new SimplePropertySource(properties)).newAdminConfig();
	}

	@Test
	void mlAdminPort() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			configure("mlAdminPort", "NaN");
		});
		assertEquals("The property mlAdminPort requires a numeric value; invalid value: ‘NaN'", exception.getMessage());
	}
}
