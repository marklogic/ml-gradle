package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.ssl.SslConfig;
import com.marklogic.client.ext.ssl.SslUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Default implementation for constructing a new instance of DatabaseClient based on the inputs in an instance of
 * DatabaseClientConfig.
 */
public class DefaultConfiguredDatabaseClientFactory implements ConfiguredDatabaseClientFactory {

	@Override
	public DatabaseClient newDatabaseClient(DatabaseClientConfig config) {
		DatabaseClientFactory.SecurityContext securityContext;

		SecurityContextType securityContextType = config.getSecurityContextType();
		DatabaseClient.ConnectionType connectionType = config.getConnectionType();

		if (SecurityContextType.BASIC.equals(securityContextType)) {
			securityContext = new DatabaseClientFactory.BasicAuthContext(config.getUsername(), config.getPassword());
		} else if (SecurityContextType.CERTIFICATE.equals(securityContextType)) {
			securityContext = buildCertificateAuthContent(config);
		} else if (SecurityContextType.DIGEST.equals(securityContextType)) {
			securityContext = new DatabaseClientFactory.DigestAuthContext(config.getUsername(), config.getPassword());
		} else if (SecurityContextType.KERBEROS.equals(securityContextType)) {
			securityContext = new DatabaseClientFactory.KerberosAuthContext(config.getExternalName());
		} else if (SecurityContextType.NONE.equals(securityContextType)) {
			securityContext = null;
		} else {
			throw new IllegalArgumentException("Unsupported SecurityContextType: " + securityContextType);
		}

		if (securityContext != null) {
			final SslConfig sslConfig = determineSslConfig(config);
			if (sslConfig != null) {
				securityContext = securityContext.withSSLContext(sslConfig.getSslContext(), sslConfig.getTrustManager());
			}

			DatabaseClientFactory.SSLHostnameVerifier verifier = config.getSslHostnameVerifier();
			if (verifier != null) {
				securityContext = securityContext.withSSLHostnameVerifier(verifier);
			}
		}

		String host = config.getHost();
		int port = config.getPort();
		String database = config.getDatabase();

		if (connectionType == null) {
			if (securityContext == null) {
				if (database == null) {
					return DatabaseClientFactory.newClient(host, port);
				}
				return DatabaseClientFactory.newClient(host, port, database);
			}
			if (database == null) {
				return DatabaseClientFactory.newClient(host, port, securityContext);
			}
			return DatabaseClientFactory.newClient(host, port, database, securityContext);
		} else {
			if (securityContext == null) {
				if (database == null) {
					return DatabaseClientFactory.newClient(host, port, null, connectionType);
				}
				return DatabaseClientFactory.newClient(host, port, database, null, connectionType);
			}
			if (database == null) {
				return DatabaseClientFactory.newClient(host, port, securityContext, connectionType);
			}
			return DatabaseClientFactory.newClient(host, port, database, securityContext, connectionType);
		}
	}

	protected DatabaseClientFactory.SecurityContext buildCertificateAuthContent(DatabaseClientConfig config) {
		X509TrustManager trustManager = config.getTrustManager();

		String certFile = config.getCertFile();
		if (certFile != null) {
			try {
				if (config.getCertPassword() != null) {
					return new DatabaseClientFactory.CertificateAuthContext(certFile, config.getCertPassword(), trustManager);
				}
				return new DatabaseClientFactory.CertificateAuthContext(certFile, trustManager);
			} catch (Exception ex) {
				throw new RuntimeException("Unable to build CertificateAuthContext: " + ex.getMessage(), ex);
			}
		}

		SslConfig sslConfig = determineSslConfig(config);
		DatabaseClientFactory.SSLHostnameVerifier verifier = config.getSslHostnameVerifier();
		return verifier != null ?
			new DatabaseClientFactory.CertificateAuthContext(sslConfig.getSslContext(), verifier, sslConfig.getTrustManager()) :
			new DatabaseClientFactory.CertificateAuthContext(sslConfig.getSslContext(), sslConfig.getTrustManager());
	}

	protected SslConfig determineSslConfig(DatabaseClientConfig config) {
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
