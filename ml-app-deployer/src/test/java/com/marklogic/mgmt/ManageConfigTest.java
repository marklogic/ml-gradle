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

import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManageConfigTest {

	@Test
	void copyConstructor() {
		ManageConfig first = new ManageConfig("host", 8003, "user", "pwd");
		first.setScheme("https");
		first.setConfigureSimpleSsl(true);
		first.setHostnameVerifier(new AllowAllHostnameVerifier());
		first.setSslContext(SimpleX509TrustManager.newSSLContext());
		first.setSslProtocol("TLSv1.3");
		first.setTrustManagementAlgorithm("something");
		first.setUseDefaultKeystore(true);
		first.setSecurityUsername("secuser");
		first.setSecurityPassword("secpwd");
		first.setSecuritySslContext(SimpleX509TrustManager.newSSLContext());
		first.setCleanJsonPayloads(true);

		ManageConfig second = new ManageConfig(first);

		assertEquals("host", second.getHost());
		assertEquals(8003, second.getPort());
		assertEquals("user", second.getUsername());
		assertEquals("pwd", second.getPassword());
		assertEquals("https", second.getScheme());
		assertTrue(second.isConfigureSimpleSsl());
		assertNotNull(second.getHostnameVerifier());
		assertNotNull(second.getSslContext());
		assertEquals("TLSv1.3", second.getSslProtocol());
		assertEquals("something", second.getTrustManagementAlgorithm());
		assertTrue(second.isUseDefaultKeystore());
		assertEquals("secuser", second.getSecurityUsername());
		assertEquals("secpwd", second.getSecurityPassword());
		assertNotNull(second.getSecuritySslContext());
		assertTrue(second.isCleanJsonPayloads());
	}
}
