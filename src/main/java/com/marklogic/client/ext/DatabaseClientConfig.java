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
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Captures all the possible inputs used to construct an instance of DatabaseClient.
 */
public class DatabaseClientConfig {

	private SecurityContextType securityContextType = SecurityContextType.DIGEST;
	private String host;
	private int port;
	private String username;
	private String password;
	private String database;

	private SSLContext sslContext;
	private String sslProtocol;
	private String trustManagementAlgorithm;
	private SSLHostnameVerifier sslHostnameVerifier;

	private String certFile;
	private String certPassword;
	private String externalName;
	private String samlToken;

	private X509TrustManager trustManager;
	private DatabaseClient.ConnectionType connectionType;

	private String cloudApiKey;
	private String basePath;

	public DatabaseClientConfig() {
	}

	public DatabaseClientConfig(String host, int port) {
		this();
		this.host = host;
		this.port = port;
	}

	public DatabaseClientConfig(String host, int port, String username, String password) {
		this(host, port);
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString() {
		return String.format("[%s@%s:%d]", username, host, port);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public void setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	public SSLHostnameVerifier getSslHostnameVerifier() {
		return sslHostnameVerifier;
	}

	public void setSslHostnameVerifier(SSLHostnameVerifier sslHostnameVerifier) {
		this.sslHostnameVerifier = sslHostnameVerifier;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public SecurityContextType getSecurityContextType() {
		return securityContextType;
	}

	public void setSecurityContextType(SecurityContextType securityContextType) {
		this.securityContextType = securityContextType;
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

	public X509TrustManager getTrustManager() {
		return trustManager;
	}

	public void setTrustManager(X509TrustManager trustManager) {
		this.trustManager = trustManager;
	}

	public DatabaseClient.ConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(DatabaseClient.ConnectionType connectionType) {
		this.connectionType = connectionType;
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

	/**
	 * @return
	 * @since 4.5.0
	 */
	public String getCloudApiKey() {
		return cloudApiKey;
	}

	/**
	 * @param cloudApiKey
	 * @since 4.5.0
	 */
	public void setCloudApiKey(String cloudApiKey) {
		this.cloudApiKey = cloudApiKey;
	}

	/**
	 * @return
	 * @since 4.5.0
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath
	 * @since 4.5.0
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * @return
	 * @since 4.5.0
	 */
	public String getSamlToken() {
		return samlToken;
	}

	/**
	 * @param samlToken
	 * @since 4.5.0
	 */
	public void setSamlToken(String samlToken) {
		this.samlToken = samlToken;
	}
}
