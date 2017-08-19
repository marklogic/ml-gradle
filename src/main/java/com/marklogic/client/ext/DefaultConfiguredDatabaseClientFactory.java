package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.ConfiguredDatabaseClientFactory;
import com.marklogic.client.ext.DatabaseClientConfig;

import javax.net.ssl.SSLContext;

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
				securityContext = securityContext.withSSLContext(sslContext);
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

	/**
	 * The Authentication shouldn't be set on the DatabaseClientConfig, but if it is, it's used instead of the value
	 * of securityContextType.
	 *
	 * @param config
	 * @return
	 */
	protected SecurityContextType determineSecurityContextType(DatabaseClientConfig config) {
		DatabaseClientFactory.Authentication auth = config.getAuthentication();
		if (auth != null) {
			if (DatabaseClientFactory.Authentication.BASIC.equals(auth)) {
				return SecurityContextType.BASIC;
			} else if (DatabaseClientFactory.Authentication.CERTIFICATE.equals(auth)) {
				return SecurityContextType.CERTIFICATE;
			} else if (DatabaseClientFactory.Authentication.KERBEROS.equals(auth)) {
				return SecurityContextType.KERBEROS;
			}
			return SecurityContextType.DIGEST;
		}
		return config.getSecurityContextType();
	}

	protected DatabaseClientFactory.SecurityContext buildCertificateAuthContent(DatabaseClientConfig config) {
		String certFile = config.getCertFile();
		if (certFile != null) {
			try {
				if (config.getCertPassword() != null) {
					return new DatabaseClientFactory.CertificateAuthContext(certFile, config.getCertPassword());
				}
				return new DatabaseClientFactory.CertificateAuthContext(certFile);
			} catch (Exception ex) {
				throw new RuntimeException("Unable to build CertificateAuthContext: " + ex.getMessage(), ex);
			}
		}
		DatabaseClientFactory.SSLHostnameVerifier verifier = config.getSslHostnameVerifier();
		if (verifier != null) {
			return new DatabaseClientFactory.CertificateAuthContext(config.getSslContext(), verifier);
		}
		return new DatabaseClientFactory.CertificateAuthContext(config.getSslContext());
	}
}
