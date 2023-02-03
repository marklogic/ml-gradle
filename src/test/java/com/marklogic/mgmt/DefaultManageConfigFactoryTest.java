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
package com.marklogic.mgmt;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
		assertEquals("my-key", ((DatabaseClientFactory.MarkLogicCloudAuthContext)bean.getSecurityContext()).getApiKey());
	}

	@Test
	void certificateAuth() {
		ManageConfig config = configure(
			"mlManageAuthentication", "certificate",
			"mlManageCertFile", "my-file.crt",
			"mlManageCertPassword", "passwd"
		);

		assertEquals("certificate", config.getAuthType());
		assertEquals("my-file.crt", config.getCertFile());
		assertEquals("passwd", config.getCertPassword());
	}

	@Test
	void kerberosAuth() {
		ManageConfig config = configure(
			"mlManageAuthentication", "kerberos",
			"mlManageExternalName", "my-name"
		);

		assertEquals("kerberos", config.getAuthType());
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

		assertEquals("saml", config.getAuthType());
		assertEquals("my-token", config.getSamlToken());

		DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.SAMLAuthContext);
		assertEquals("my-token", ((DatabaseClientFactory.SAMLAuthContext)bean.getSecurityContext()).getToken());
	}

	@Test
	void sslHostnameVerifier() {
		ManageConfig config = configure("mlManageSslHostnameVerifier", "common");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.COMMON, config.getSslHostnameVerifier());

		config = configure("mlManageSslHostnameVerifier", "ANY");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.ANY, config.getSslHostnameVerifier());

		config = configure("mlManageSslHostnameVerifier", "strICT");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.STRICT, config.getSslHostnameVerifier());

		assertThrows(IllegalArgumentException.class, () -> configure("mlManageSslHostnameVerifier", "bogus"));
	}

	@Test
	void mlSslHostnameVerifier() {
		ManageConfig config = configure("mlSslHostnameVerifier", "any");
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.ANY, config.getSslHostnameVerifier());

		config = configure(
			"mlSslHostnameVerifier", "any",
			"mlManageSslHostnameVerifier", "strict"
		);
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.STRICT, config.getSslHostnameVerifier());
	}

	@Test
	void mlAuthentication() {
		ManageConfig config = configure("mlAuthentication", "cloud");
		assertEquals("cloud", config.getAuthType());

		config = configure(
			"mlAuthentication", "cloud",
			"mlManageAuthentication", "basic"
		);
		assertEquals("basic", config.getAuthType());
	}

	@Test
	void mlManageBasePath() {
		ManageConfig config = configure(
			"mlManageBasePath", "/my/custom/manage/path"
		);
		assertEquals("/my/custom/manage/path", config.getBasePath(),
			"If a user only specifies mlManageBasePath, then the assumption is that they're using a reverse proxy and " +
				"have defined their own custom path for the Manage app server. They could be using ML Cloud, but " +
				"that's not likely as it would make more sense to still define mlCloudBasePath and then set " +
				"mlManageBasePath to the custom Manage part (as a user is not allowed to setup a base path in ML Cloud " +
				"that doesn't begin with their common base path).");
	}

	@Test
	void mlCloudBasePath() {
		ManageConfig config = configure(
			"mlCloudBasePath", "/my/domain"
		);
		assertEquals("/my/domain/manage", config.getBasePath(),
			"If a user only specifies mlCloudBasePath, then the assumption is that they're good to go with the default " +
				"Manage base path setup in ML Cloud, and so they only need to define the 'cloud base path' that occurs " +
				"before '/manage'");
	}

	@Test
	void mlCloudBasePathWithManageBasePath() {
		ManageConfig config = configure(
			"mlCloudBasePath", "/my/domain",
			"mlManageBasePath", "/my-custom-manage-path"
		);
		assertEquals("/my/domain/my-custom-manage-path", config.getBasePath(),
			"If a user specifies both mlCloudBasePath and mlManageBasePath, then the assumption is that they've " +
				"changed the default Manage base path but it still begins with the common base path defined by " +
				"mlCloudBasePath.");
	}

	private ManageConfig configure(String... properties) {
		return new DefaultManageConfigFactory(new SimplePropertySource(properties)).newManageConfig();
	}
}
