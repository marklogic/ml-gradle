package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.ConfiguredDatabaseClientFactory;
import com.marklogic.client.ext.DatabaseClientConfig;

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
		}
		else {
			throw new IllegalArgumentException("Unsupported SecurityContextType: " + securityContextType);
		}

		if (securityContext != null) {
			SSLContext sslContext = config.getSslContext();
			DatabaseClientFactory.SSLHostnameVerifier verifier = config.getSslHostnameVerifier();
			if (sslContext != null) {
				securityContext = securityContext.withSSLContext(sslContext, config.getTrustManager());
			}
			if (verifier != null) {
				securityContext = securityContext.withSSLHostnameVerifier(verifier);
			}
		}

		String host = config.getHost();
		int port = config.getPort();
		String database = config.getDatabase();

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

		DatabaseClientFactory.SSLHostnameVerifier verifier = config.getSslHostnameVerifier();

		if (verifier != null) {
			return new DatabaseClientFactory.CertificateAuthContext(config.getSslContext(), verifier, trustManager);
		}

		return new DatabaseClientFactory.CertificateAuthContext(config.getSslContext(), trustManager);
	}
}
