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
