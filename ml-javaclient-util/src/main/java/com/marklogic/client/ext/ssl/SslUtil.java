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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public abstract class SslUtil {

	// Defaulting this to "TLS" in 5.1.0 so that clients connecting to MarkLogic 12 will default to TLSv1.3, while
	// clients connecting to MarkLogic 11 or older will default to TLSv1.2.
	public final static String DEFAULT_SSL_PROTOCOL = "TLS";

	private static final Logger LOGGER = LoggerFactory.getLogger(SslUtil.class);

	/**
	 * Configure an SSLContext and X509TrustManager with the default protocol and the default algorithm of
	 * TrustManagerFactory.
	 *
	 * @return an SslConfig object based on default settings
	 */
	public static SslConfig configureUsingTrustManagerFactory() {
		return configureUsingTrustManagerFactory(DEFAULT_SSL_PROTOCOL, null);
	}

	/**
	 * Configure an SSLContext and X509TrustManager with the given inputs.
	 *
	 * @param protocol  the protocol to use when getting an instance of an SSLContext
	 * @param algorithm an optional algorithm to use for getting an instance of TrustManagerFactory; if not specified,
	 *                  the default algorithm of TrustManagerFactory is used
	 * @return an SslConfig based on the given inputs
	 */
	public static SslConfig configureUsingTrustManagerFactory(String protocol, String algorithm) {
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance(protocol);
		} catch (NoSuchAlgorithmException e) {
			// Including this to make Polaris happy.
			String message = String.format("Unable to instantiate SSLContext with protocol: %s; cause: %s", protocol, e.getMessage());
			LOGGER.error(message, e);
			throw new RuntimeException(message, e);
		}

		if (algorithm == null || algorithm.trim().length() < 1) {
			algorithm = TrustManagerFactory.getDefaultAlgorithm();
		}
		TrustManagerFactory trustManagerFactory;
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			// Including this to make Polaris happy.
			String message = String.format("Unable to instantiate TrustManagerFactory, cause: %s", e.getMessage());
			LOGGER.error(message, e);
			throw new RuntimeException(message, e);
		}

		try {
			trustManagerFactory.init((KeyStore) null);
		} catch (KeyStoreException e) {
			// Including this to make Polaris happy.
			String message = String.format("Unable to initialize TrustManagerFactory, cause: %s", e.getMessage());
			LOGGER.error(message, e);
			throw new RuntimeException(message, e);
		}

		X509TrustManager x509TrustManager = null;
		for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
			if (tm instanceof X509TrustManager) {
				x509TrustManager = (X509TrustManager) tm;
				break;
			}
		}
		if (x509TrustManager == null) {
			throw new RuntimeException("Could not initialize an SSLContext; unable to find an X509TrustManager in the " +
				"TrustManagerFactory instantiated with algorithm: " + algorithm);
		}

		try {
			sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
		} catch (KeyManagementException e) {
			// Including this to make Polaris happy.
			String message = String.format("Unable to initialize SSLContext, cause: %s", e.getMessage());
			LOGGER.error(message, e);
			throw new RuntimeException(message, e);
		}

		return new SslConfig(sslContext, x509TrustManager);
	}

}

