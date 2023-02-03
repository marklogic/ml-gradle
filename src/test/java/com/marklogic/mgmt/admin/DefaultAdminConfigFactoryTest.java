/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.admin;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.mgmt.util.SimplePropertySource;
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

	private AdminConfig configure(String... properties) {
		return new DefaultAdminConfigFactory(new SimplePropertySource(properties)).newAdminConfig();
	}
}
