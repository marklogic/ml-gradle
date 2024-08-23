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
package com.marklogic.rest.util;

import com.marklogic.junit.BaseTestHelper;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Many of these tests are just smoke tests that can be used to inspect logging as well. Ideally, they can soon depend
 * on the Manage server being configured to use SSL. Although testing the use of the default keystore would likely
 * still be out of scope due to the difficulty of using a certificate signed by an authority that's trusted by the
 * default keystore.
 */
public class RestTemplateUtilTest extends BaseTestHelper {

	private boolean configurerInvoked = false;
	private ManageConfig manageConfig = new ManageConfig();

	@BeforeEach
	void setup() {
		manageConfig.setUsername("someuser");
		manageConfig.setPassword("someword");
	}

	@Test
	@Deprecated
	public void configurerList() {
		assertEquals(4, RestTemplateUtil.DEFAULT_CONFIGURERS.size());

		assertFalse(configurerInvoked);
		HttpClientBuilderConfigurer configurer = (restConfig, builder) -> {
			logger.info("Just a test of adding a configurer");
			configurerInvoked = true;
			return builder;
		};

		new ManageClient(manageConfig);
		assertFalse(configurerInvoked, "The configurer should not be invoked now that ManageClient uses OkHttp " +
			"instead of Apache HTTP");

		RestTemplateUtil.newRestTemplate(manageConfig, configurer);
		assertTrue(configurerInvoked, "The configurer should have been invoked since a deprecated newRestTemplate " +
			"method was used that still honors HttpClientBuilderConfigurer instances");
	}

	@Test
	public void configureSimpleSsl() {
		manageConfig.setConfigureSimpleSsl(true);
		new ManageClient(manageConfig).getRestTemplate();
	}

	@Test
	public void simpleSslWithCustomProtocol() {
		manageConfig.setConfigureSimpleSsl(true);
		manageConfig.setSslProtocol("SSLv3");
		new ManageClient(manageConfig).getRestTemplate();
	}

	@Test
	public void simpleSslWithInvalidProtocol() {
		manageConfig.setConfigureSimpleSsl(true);
		manageConfig.setSslProtocol("invalid");
		ManageClient client = new ManageClient(manageConfig);
		try {
			client.getRestTemplate();
			fail("Expected failure due to invalid protocol");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	public void customSslContextAndHostnameVerifier() throws Exception {
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();

		manageConfig.setSslContext(sslContext);
		manageConfig.setHostnameVerifier(new AllowAllHostnameVerifier());

		new ManageClient(manageConfig).getRestTemplate();
	}

	@Test
	public void useDefaultKeystore() {
		manageConfig.setUseDefaultKeystore(true);
		new ManageClient(manageConfig).getRestTemplate();
	}

	@Test
	public void defaultKeystoreWithInvalidProtocol() {
		manageConfig.setUseDefaultKeystore(true);
		manageConfig.setSslProtocol("invalid");
		ManageClient client = new ManageClient(manageConfig);
		try {
			client.getRestTemplate();
			fail("Expected failure due to invalid protocol");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	public void defaultKeystoreWithInvalidAlgorithm() {
		manageConfig.setUseDefaultKeystore(true);
		manageConfig.setTrustManagementAlgorithm("invalid");
		ManageClient client = new ManageClient(manageConfig);
		try {
			client.getRestTemplate();
			fail("Expected failure due to invalid algorithm");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	void noUsername() {
		manageConfig.setUsername(null);
		RuntimeException ex = assertThrows(RuntimeException.class,
			() -> RestTemplateUtil.newRestTemplate(manageConfig));

		assertEquals("Unable to connect to the MarkLogic app server at http://localhost:8002; cause: username must be of type String",
			ex.getMessage(),
			"As of 4.5.0, since auth strategies other than basic/digest are now supported, the error message is expected " +
				"to identify which MarkLogic app server is being accessed but not any authentication details. This is " +
				"due to a change to toString of RestConfig/ManageConfig so that a username is not logged.");
	}
}
