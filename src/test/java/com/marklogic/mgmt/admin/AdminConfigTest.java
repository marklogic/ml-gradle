package com.marklogic.mgmt.admin;

import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminConfigTest {

	@Test
	void copyConstructor() {
		AdminConfig first = new AdminConfig("host", 8003, "user", "pwd");
		first.setScheme("https");
		first.setConfigureSimpleSsl(true);
		first.setHostnameVerifier(new AllowAllHostnameVerifier());
		first.setSslContext(SimpleX509TrustManager.newSSLContext());
		first.setSslProtocol("TLSv1.3");
		first.setTrustManagementAlgorithm("something");
		first.setUseDefaultKeystore(true);

		AdminConfig second = new AdminConfig(first);
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
	}
}
