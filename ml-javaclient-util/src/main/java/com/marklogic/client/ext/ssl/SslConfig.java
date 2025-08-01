/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Captures the output of functions in SslUtil so that they can be easily used when constructing a DatabaseClient,
 * which requires the X509TrustManager to be an input separate from the SSLContext.
 */
public class SslConfig {

	private SSLContext sslContext;
	private X509TrustManager trustManager;

	public SslConfig(SSLContext sslContext, X509TrustManager trustManager) {
		this.sslContext = sslContext;
		this.trustManager = trustManager;
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public X509TrustManager getTrustManager() {
		return trustManager;
	}
}
