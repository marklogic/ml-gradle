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
package com.marklogic.rest.util;

import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import com.marklogic.client.ext.ssl.SslConfig;
import com.marklogic.client.ext.ssl.SslUtil;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URISyntaxException;

public class RestConfig {

	private String host;
	private int port;
	// Defaulting this for backwards-compatibility reasons in 4.5.0
	private String authType = "digest";
	private String username;
	private String password;
	private String cloudApiKey;
	private String certFile;
	private String certPassword;
	private String externalName;
	private String samlToken;

	private String basePath;
	private String scheme = "http";

	private boolean configureSimpleSsl;
	private boolean useDefaultKeystore;
	private String sslProtocol;
	private String trustManagementAlgorithm;
	private DatabaseClientFactory.SSLHostnameVerifier sslHostnameVerifier;
	private SSLContext sslContext;
	@Deprecated
	private X509HostnameVerifier hostnameVerifier;

	public RestConfig() {
	}

	public RestConfig(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public RestConfig(RestConfig other) {
		this(other.host, other.port, other.username, other.password);
		if (other.scheme != null) {
			this.scheme = other.scheme;
		}

		this.cloudApiKey = other.cloudApiKey;
		this.basePath = other.basePath;

		this.configureSimpleSsl = other.configureSimpleSsl;
		this.useDefaultKeystore = other.useDefaultKeystore;
		this.sslProtocol = other.sslProtocol;
		this.trustManagementAlgorithm = other.trustManagementAlgorithm;
		this.sslContext = other.sslContext;
		this.hostnameVerifier = other.hostnameVerifier;
		this.sslHostnameVerifier = other.sslHostnameVerifier;
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
			.withSSLHostnameVerifier(getSslHostnameVerifier());

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
					.withTrustManager(new SimpleX509TrustManager());
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

	/**
	 * Using the java.net.URI constructor that takes a string. Using any other constructor runs into encoding problems,
	 * e.g. when a mimetype has a plus in it, that plus needs to be encoded, but doing as %2B will result in the % being
	 * double encoded. Unfortunately, it seems some encoding is still needed - e.g. for a pipeline like "Flexible Replication"
	 * with a space in its name, the space must be encoded properly as a "+".
	 *
	 * @param path
	 * @return
	 */
	public URI buildUri(String path) {
		String basePathToAppend = "";
		if (basePath != null) {
			if (!basePath.startsWith("/")) {
				basePathToAppend = "/";
			}
			basePathToAppend += basePath;
			if (path.startsWith("/") && basePathToAppend.endsWith("/")) {
				basePathToAppend = basePathToAppend.substring(0, basePathToAppend.length() - 1);
			}
		}
		try {
			return new URI(String.format("%s://%s:%d%s%s", getScheme(), getHost(), getPort(), basePathToAppend, path.replace(" ", "+")));
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Unable to build URI for path: " + path + "; cause: " + ex.getMessage(), ex);
		}
	}

	public String getBaseUrl() {
		return String.format("%s://%s:%d", scheme, host, port);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
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

	@Deprecated
	public X509HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

	@Deprecated
	public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
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

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
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
}
