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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public abstract class SslUtil {

	public final static String DEFAULT_SSL_PROTOCOL = "TLSv1.2";

	/**
	 * Configure an SSLContext and X509TrustManager with TLSv1.2 as the default protocol and the default algorithm of
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
			throw new RuntimeException("Unable to instantiate SSLContext with protocol: " + protocol + "; cause: " + e.getMessage(), e);
		}

		if (algorithm == null || algorithm.trim().length() < 1) {
			algorithm = TrustManagerFactory.getDefaultAlgorithm();
		}
		TrustManagerFactory trustManagerFactory;
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to instantiate TrustManagerFactory, cause: " + e.getMessage(), e);
		}

		try {
			trustManagerFactory.init((KeyStore) null);
		} catch (KeyStoreException e) {
			throw new RuntimeException("Unable to initialize TrustManagerFactory, cause: " + e.getMessage(), e);
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
			throw new RuntimeException("Unable to initialize SSLContext, cause: " + e.getMessage(), e);
		}

		return new SslConfig(sslContext, x509TrustManager);
	}

}

