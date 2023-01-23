package com.marklogic.rest.util;

import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestConfigTest {

	@Test
	void copyConstructor() {
		RestConfig first = new RestConfig("host", 8003, "user", "pwd");
		first.setScheme("https");
		first.setConfigureSimpleSsl(true);
		first.setHostnameVerifier(new AllowAllHostnameVerifier());
		first.setSslContext(SimpleX509TrustManager.newSSLContext());
		first.setSslProtocol("TLSv1.3");
		first.setTrustManagementAlgorithm("something");
		first.setUseDefaultKeystore(true);
		first.setCloudApiKey("my-key");
		first.setBasePath("/my/path");

		RestConfig second = new RestConfig(first);
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
		assertEquals("my-key", second.getCloudApiKey());
		assertEquals("/my/path", second.getBasePath());
	}

	@Test
	void buildUriWithBasePath() {
		final String targetPath = "/target/path";
		RestConfig config = new RestConfig("somehost", 8002, "doesnt", "matter");
		assertEquals("http://somehost:8002/target/path", config.buildUri(targetPath).toString());

		Stream.of("/my/base", "/my/base/", "my/base", "my/base/").forEach(basePath -> {
			config.setBasePath(basePath);
			assertEquals("http://somehost:8002/my/base/target/path", config.buildUri(targetPath).toString(),
				"Unexpected response using basePath: " + basePath);
		});

		config.setScheme("https");
		config.setBasePath("/secure");
		assertEquals("https://somehost:8002/secure/target/path", config.buildUri(targetPath).toString());
	}
}
