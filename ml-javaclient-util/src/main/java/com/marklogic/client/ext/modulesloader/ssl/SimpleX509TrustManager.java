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
package com.marklogic.client.ext.modulesloader.ssl;

import com.marklogic.client.ext.ssl.SslUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * "Simple" in that it doesn't do anything. Useful for development, but you should consider something more robust for a
 * production environment, though if you're only using this for loading modules, it may suffice.
 */
public class SimpleX509TrustManager implements X509TrustManager {

	/**
	 * Factory method for creating a simple SSLContext that uses this class as its TrustManager.
	 *
	 * @return a default trust-everything SSLContext
	 */
	public static SSLContext newSSLContext() {
		return newSSLContext(SslUtil.DEFAULT_SSL_PROTOCOL);
	}

	public static SSLContext newSSLContext(String protocol) {
		try {
			SSLContext sslContext = SSLContext.getInstance(protocol);
			sslContext.init(null, new TrustManager[]{new SimpleX509TrustManager()}, null);
			return sslContext;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

}
