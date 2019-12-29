package com.marklogic.client.ext.ssl;

import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

public class SslUtilTest extends Assert {

	@Test
	public void configureWithDefaults() {
		SslConfig config = SslUtil.configureUsingTrustManagerFactory();
		X509Certificate[] certs = config.getTrustManager().getAcceptedIssuers();
		assertTrue(certs.length > 0);

		SSLContext context = config.getSslContext();
		assertEquals("TLSv1.2", context.getProtocol());
	}

	@Test
	public void configureWithCustomProtocol() {
		SslConfig config = SslUtil.configureUsingTrustManagerFactory("SSLv3", null);
		assertEquals("SSLv3", config.getSslContext().getProtocol());
	}

	@Test
	public void invalidProtocol() {
		try {
			SslUtil.configureUsingTrustManagerFactory("invalid", null);
			fail("An exception should have been thrown due to an invalid SSL protocol being passed in");
		} catch (Exception ex) {
			System.out.println("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	public void invalidAlgorithm() {
		try {
			SslUtil.configureUsingTrustManagerFactory("TLSv1.2", "invalid");
			fail("An exception should have been thrown due to an invalid algorithm being passed in");
		} catch (Exception ex) {
			System.out.println("Caught expected exception: " + ex.getMessage());
		}
	}

}
