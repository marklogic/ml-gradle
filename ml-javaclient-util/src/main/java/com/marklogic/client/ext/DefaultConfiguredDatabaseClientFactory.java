/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
			.withOAuth(config.getOauthToken())
			.withCloudApiKey(config.getCloudApiKey())
			.withSSLProtocol(config.getSslProtocol())
			.withSSLHostnameVerifier(config.getSslHostnameVerifier())
			// The following 8 were added for 4.7.0 based on Java Client 6.5.0
			.withKeyStorePath(config.getKeyStorePath())
			.withKeyStorePassword(config.getKeyStorePassword())
			.withKeyStoreType(config.getKeyStoreType())
			.withKeyStoreAlgorithm(config.getKeyStoreAlgorithm())
			.withTrustStorePath(config.getTrustStorePath())
			.withTrustStorePassword(config.getTrustStorePassword())
			.withTrustStoreType(config.getTrustStoreType())
			.withTrustStoreAlgorithm(config.getTrustStoreAlgorithm());

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
