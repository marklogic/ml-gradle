/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;

public class SslUtilTest {

	@Test
	public void configureWithDefaults() {
		SslConfig config = SslUtil.configureUsingTrustManagerFactory();
		X509Certificate[] certs = config.getTrustManager().getAcceptedIssuers();
		assertTrue(certs.length > 0);

		SSLContext context = config.getSslContext();
		assertEquals("TLS", context.getProtocol(), "As of 6.0.0, the default is TLS so that clients connecting to " +
			"MarkLogic 12 will default to TLSv1.3.");
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
