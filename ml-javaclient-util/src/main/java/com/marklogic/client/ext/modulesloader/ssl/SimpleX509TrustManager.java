/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
