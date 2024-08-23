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
