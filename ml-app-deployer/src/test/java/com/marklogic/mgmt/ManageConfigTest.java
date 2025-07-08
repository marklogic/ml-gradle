/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManageConfigTest {

	@Test
	void copyConstructor() {
		ManageConfig first = new ManageConfig("host", 8003, "user", "pwd");
		first.setScheme("https");
		first.setConfigureSimpleSsl(true);
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
