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
package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.ext.ssl.SslConfig;
import com.marklogic.client.ext.ssl.SslUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Default implementation for constructing a new instance of DatabaseClient based on the inputs in an instance of
 * DatabaseClientConfig.
 * <p>
 * As of 4.5.0, this now uses the new DatabaseClientBuilder class in the Java Client to handle the heavy lifting of
 * figuring out how to construct a DatabaseClient.
 */
public class DefaultConfiguredDatabaseClientFactory implements ConfiguredDatabaseClientFactory {

	@Override
	public DatabaseClient newDatabaseClient(DatabaseClientConfig config) {
		DatabaseClientBuilder builder = new DatabaseClientBuilder()
			.withHost(config.getHost())
			.withPort(config.getPort())
			.withBasePath(config.getBasePath())
			.withDatabase(config.getDatabase())
			.withConnectionType(config.getConnectionType())
			.withUsername(config.getUsername())
			.withPassword(config.getPassword())
			.withCertificateFile(config.getCertFile())
			.withCertificatePassword(config.getCertPassword())
			.withKerberosPrincipal(config.getExternalName())
			.withSAMLToken(config.getSamlToken())
			.withCloudApiKey(config.getCloudApiKey())
			.withSSLProtocol(config.getSslProtocol())
			.withSSLHostnameVerifier(config.getSslHostnameVerifier());

		if (config.getSecurityContextType() != null) {
			builder.withAuthType(config.getSecurityContextType().name());
		}

		SslConfig sslConfig = determineSslConfig(config);
		if (sslConfig != null) {
			builder
				.withSSLContext(sslConfig.getSslContext())
				.withTrustManager(sslConfig.getTrustManager());
		}

		return builder.build();
	}

	private SslConfig determineSslConfig(DatabaseClientConfig config) {
		SSLContext sslContext = config.getSslContext();
		X509TrustManager trustManager = config.getTrustManager();
		if (sslContext != null && trustManager != null) {
			return new SslConfig(sslContext, trustManager);
		}

		final String protocol = config.getSslProtocol();
		return protocol != null && protocol.trim().length() > 0 ?
			SslUtil.configureUsingTrustManagerFactory(protocol, config.getTrustManagementAlgorithm()) :
			null;
	}
}
