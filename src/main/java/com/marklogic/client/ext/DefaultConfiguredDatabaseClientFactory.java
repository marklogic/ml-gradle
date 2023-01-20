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
			.withCloudApiKey(config.getCloudApiKey())
			.withSSLProtocol(config.getSslProtocol())
			.withSSLHostnameVerifier(config.getSslHostnameVerifier());

		if (config.getSecurityContextType() != null) {
			builder.withSecurityContextType(config.getSecurityContextType().name());
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
