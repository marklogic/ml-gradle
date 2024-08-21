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
package com.marklogic.rest.util.configurer;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.ssl.SslUtil;
import com.marklogic.rest.util.HttpClientBuilderConfigurer;
import com.marklogic.rest.util.RestConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.SSLContext;

/**
 * @deprecated since 4.5.0; OkHttp is now the preferred client
 */
@Deprecated
public class SslConfigurer extends LoggingObject implements HttpClientBuilderConfigurer {

	/**
	 * First checks for a custom SSLContext; then checks to see if the default keystore should be used; then checks to
	 * see if a simple "trust everything" approach should be used.
	 *
	 * @param config
	 * @param httpClientBuilder
	 * @return
	 */
	@Override
	public HttpClientBuilder configureHttpClientBuilder(RestConfig config, HttpClientBuilder httpClientBuilder) {
		SSLContext sslContext = null;
		if (config.getSslContext() != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Using custom SSLContext for connecting to: " + config.getBaseUrl());
			}
			sslContext = config.getSslContext();
		} else if (config.isUseDefaultKeystore()) {
			sslContext = buildSslContextViaTrustManagerFactory(config);
		} else if (config.isConfigureSimpleSsl()) {
			sslContext = buildSimpleSslContext(config);
		}

		if (sslContext != null) {
			httpClientBuilder.setSslcontext(sslContext);

			if (config.getHostnameVerifier() != null) {
				if (logger.isInfoEnabled()) {
					logger.info("Using custom X509HostnameVerifier for connecting to: " + config.getBaseUrl());
				}
				httpClientBuilder.setHostnameVerifier(config.getHostnameVerifier());
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("Using 'allow all' X509HostnameVerifier for connecting to: " + config.getBaseUrl());
				}
				httpClientBuilder.setHostnameVerifier(new AllowAllHostnameVerifier());
			}
		}

		return httpClientBuilder;
	}

	protected SSLContext buildSslContextViaTrustManagerFactory(RestConfig config) {
		final String protocol = determineProtocol(config);
		final String algorithm = config.getTrustManagementAlgorithm();
		if (logger.isInfoEnabled()) {
			logger.info("Using default keystore with SSL protocol " + protocol + " for connecting to: " + config.getBaseUrl());
		}
		return SslUtil.configureUsingTrustManagerFactory(protocol, algorithm).getSslContext();
	}

	protected SSLContext buildSimpleSslContext(RestConfig config) {
		final String protocol = determineProtocol(config);

		SSLContextBuilder builder = new SSLContextBuilder().useProtocol(protocol);
		if (logger.isInfoEnabled()) {
			logger.info("Configuring simple SSL approach with protocol " + protocol + " for connecting to: " + config.getBaseUrl());
		}
		try {
			return builder.loadTrustMaterial(null, (chain, authType) -> true).build();
		} catch (Exception ex) {
			throw new RuntimeException("Unable to configure simple SSLContext for connecting to: " + config.getBaseUrl() + ", cause: " + ex.getMessage(), ex);
		}
	}

	protected String determineProtocol(RestConfig config) {
		String protocol = config.getSslProtocol();
		if (StringUtils.isEmpty(protocol)) {
			protocol = SslUtil.DEFAULT_SSL_PROTOCOL;
		}
		return protocol;
	}
}
