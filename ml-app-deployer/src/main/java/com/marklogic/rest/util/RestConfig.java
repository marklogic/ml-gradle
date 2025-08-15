/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util;

import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import com.marklogic.client.ext.ssl.SslConfig;
import com.marklogic.client.ext.ssl.SslUtil;
import okhttp3.OkHttpClient;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URISyntaxException;

public class RestConfig extends UriConfig {

	// Defaulting this for backwards-compatibility reasons in 4.5.0
	private String authType = "digest";
	private String username;
	private String password;
	private String cloudApiKey;
	private String certFile;
	private String certPassword;
	private String externalName;
	private String samlToken;
	private String oauthToken;

	private boolean configureSimpleSsl;
	private boolean useDefaultKeystore;
	private String sslProtocol;
	private String trustManagementAlgorithm;
	private DatabaseClientFactory.SSLHostnameVerifier sslHostnameVerifier;
	private SSLContext sslContext;

	// Added in 4.7.0 for 2-way SSL.
	private String keyStorePath;
	private String keyStorePassword;
	private String keyStoreType;
	private String keyStoreAlgorithm;
	private String trustStorePath;
	private String trustStorePassword;
	private String trustStoreType;
	private String trustStoreAlgorithm;

	// Added in 6.0.0 as an extension mechanism until this is available
	// in DatabaseClientBuilder in the MarkLogic Java Client.
	private DatabaseClientFactory.ClientConfigurator<OkHttpClient.Builder> clientConfigurator;

	public RestConfig() {
	}

	public RestConfig(String host, int port, String username, String password) {
		super(host, port);
		this.username = username;
		this.password = password;
	}

	public RestConfig(RestConfig other) {
		this(other.getHost(), other.getPort(), other.username, other.password);
		if (other.getScheme() != null) {
			setScheme(other.getScheme());
		}

		this.cloudApiKey = other.cloudApiKey;
		setBasePath(other.getBasePath());

		this.authType = other.authType;
		this.certFile = other.certFile;
		this.certPassword = other.certPassword;
		this.externalName = other.externalName;
		this.samlToken = other.samlToken;

		this.configureSimpleSsl = other.configureSimpleSsl;
		this.useDefaultKeystore = other.useDefaultKeystore;
		this.sslProtocol = other.sslProtocol;
		this.trustManagementAlgorithm = other.trustManagementAlgorithm;
		this.sslContext = other.sslContext;
		this.sslHostnameVerifier = other.sslHostnameVerifier;

		this.keyStorePath = other.keyStorePath;
		this.keyStorePassword = other.keyStorePassword;
		this.keyStoreAlgorithm = other.keyStoreAlgorithm;
		this.keyStoreType = other.keyStoreType;
		this.trustStorePath = other.trustStorePath;
		this.trustStorePassword = other.trustStorePassword;
		this.trustStoreType = other.trustStoreType;
		this.trustStoreAlgorithm = other.trustStoreAlgorithm;
	}

	public DatabaseClientBuilder newDatabaseClientBuilder() {
		DatabaseClientBuilder builder = new DatabaseClientBuilder()
			.withHost(getHost())
			.withPort(getPort())
			.withBasePath(getBasePath())
			.withAuthType(getAuthType())
			.withUsername(getUsername())
			.withPassword(getPassword())
			.withCloudApiKey(getCloudApiKey())
			.withCertificateFile(getCertFile())
			.withCertificatePassword(getCertPassword())
			.withKerberosPrincipal(getExternalName())
			.withSAMLToken(getSamlToken())
			.withOAuthToken(getOauthToken())
			.withSSLHostnameVerifier(getSslHostnameVerifier())
			// These 8 were added in 4.7.0. They do not conflict with the SSL config below; if the user is setting
			// these, they won't have a reason to provide their own SSLContext nor request that the default keystore
			// be used or simple SSL be used.
			.withKeyStorePath(getKeyStorePath())
			.withKeyStorePassword(getKeyStorePassword())
			.withKeyStoreType(getKeyStoreType())
			.withKeyStoreAlgorithm(getKeyStoreAlgorithm())
			.withTrustStorePath(getTrustStorePath())
			.withTrustStorePassword(getTrustStorePassword())
			.withTrustStoreType(getTrustStoreType())
			.withTrustStoreAlgorithm(getTrustStoreAlgorithm());

		if (getSslContext() != null) {
			builder.withSSLContext(getSslContext());
		} else {
			String sslProtocol = getSslProtocol();
			// The MarkLogic Java Client will default to using the JVM's default trust manager if none is specified.
			// This block though honors the existing "use default keystore" option, which allows for the user to also
			// specify a trust management algorithm.
			if (isUseDefaultKeystore()) {
				sslProtocol = StringUtils.hasText(sslProtocol) ? sslProtocol : SslUtil.DEFAULT_SSL_PROTOCOL;
				SslConfig sslConfig = SslUtil.configureUsingTrustManagerFactory(sslProtocol, getTrustManagementAlgorithm());
				builder
					.withSSLContext(sslConfig.getSslContext())
					.withTrustManager(sslConfig.getTrustManager());
			} else if (isConfigureSimpleSsl()) {
				builder
					.withSSLContext(StringUtils.hasText(sslProtocol) ?
						SimpleX509TrustManager.newSSLContext(sslProtocol) :
						SimpleX509TrustManager.newSSLContext())
					.withTrustManager(new SimpleX509TrustManager())
					.withSSLHostnameVerifier(SSLHostnameVerifier.ANY);
			} else {
				builder.withSSLProtocol(sslProtocol);
			}
		}

		return builder;
	}

	@Override
	public String toString() {
		return String.format("%s://%s:%d", getScheme(), getHost(), getPort());
	}

	@Deprecated
	public String getBaseUrl() {
		return String.format("%s://%s:%d", getScheme(), getHost(), getPort());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isConfigureSimpleSsl() {
		return configureSimpleSsl;
	}

	public void setConfigureSimpleSsl(boolean configureSimpleSsl) {
		this.configureSimpleSsl = configureSimpleSsl;
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public void setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	public String getSslProtocol() {
		return sslProtocol;
	}

	public void setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
	}

	public String getTrustManagementAlgorithm() {
		return trustManagementAlgorithm;
	}

	public void setTrustManagementAlgorithm(String trustManagementAlgorithm) {
		this.trustManagementAlgorithm = trustManagementAlgorithm;
	}

	public boolean isUseDefaultKeystore() {
		return useDefaultKeystore;
	}

	public void setUseDefaultKeystore(boolean useDefaultKeystore) {
		this.useDefaultKeystore = useDefaultKeystore;
	}

	public DatabaseClientFactory.SSLHostnameVerifier getSslHostnameVerifier() {
		return sslHostnameVerifier;
	}

	public void setSslHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier sslHostnameVerifier) {
		this.sslHostnameVerifier = sslHostnameVerifier;
	}

	public String getCloudApiKey() {
		return cloudApiKey;
	}

	public void setCloudApiKey(String cloudApiKey) {
		this.cloudApiKey = cloudApiKey;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getCertFile() {
		return certFile;
	}

	public void setCertFile(String certFile) {
		this.certFile = certFile;
	}

	public String getCertPassword() {
		return certPassword;
	}

	public void setCertPassword(String certPassword) {
		this.certPassword = certPassword;
	}

	public String getExternalName() {
		return externalName;
	}

	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}

	public String getSamlToken() {
		return samlToken;
	}

	public void setSamlToken(String samlToken) {
		this.samlToken = samlToken;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getKeyStorePath() {
		return keyStorePath;
	}

	/**
	 * @param keyStorePath
	 * @since 4.7.0
	 */
	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * @param keyStorePassword
	 * @since 4.7.0
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getKeyStoreType() {
		return keyStoreType;
	}

	/**
	 * @param keyStoreType
	 * @since 4.7.0
	 */
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getKeyStoreAlgorithm() {
		return keyStoreAlgorithm;
	}

	/**
	 * @param keyStoreAlgorithm
	 * @since 4.7.0
	 */
	public void setKeyStoreAlgorithm(String keyStoreAlgorithm) {
		this.keyStoreAlgorithm = keyStoreAlgorithm;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getTrustStorePath() {
		return trustStorePath;
	}

	/**
	 * @param trustStorePath
	 * @since 4.7.0
	 */
	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	/**
	 * @param trustStorePassword
	 * @since 4.7.0
	 */
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getTrustStoreType() {
		return trustStoreType;
	}

	/**
	 * @param trustStoreType
	 * @since 4.7.0
	 */
	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}

	/**
	 * @return
	 * @since 4.7.0
	 */
	public String getTrustStoreAlgorithm() {
		return trustStoreAlgorithm;
	}

	/**
	 * @param trustStoreAlgorithm
	 * @since 4.7.0
	 */
	public void setTrustStoreAlgorithm(String trustStoreAlgorithm) {
		this.trustStoreAlgorithm = trustStoreAlgorithm;
	}

	/**
	 * @since 6.0.0
	 */
	public String getOauthToken() {
		return oauthToken;
	}

	/**
	 * @since 6.0.0
	 */
	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}

	protected DatabaseClientFactory.ClientConfigurator<OkHttpClient.Builder> getClientConfigurator() {
		return clientConfigurator;
	}

	protected void setClientConfigurator(DatabaseClientFactory.ClientConfigurator<OkHttpClient.Builder> clientConfigurator) {
		this.clientConfigurator = clientConfigurator;
	}
}
