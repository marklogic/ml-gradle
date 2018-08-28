package com.marklogic.rest.util.configurer;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.rest.util.HttpClientBuilderConfigurer;
import com.marklogic.rest.util.RestConfig;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.security.cert.X509Certificate;

public class SslConfigurer extends LoggingObject implements HttpClientBuilderConfigurer {

	@Override
	public HttpClientBuilder configureHttpClientBuilder(RestConfig config, HttpClientBuilder httpClientBuilder) {
		if (config.isConfigureSimpleSsl()) {
			if (logger.isInfoEnabled()) {
				logger.info("Configuring simple SSL approach for connecting to: " + config.getBaseUrl());
			}
			configureSimpleSsl(httpClientBuilder);
		}

		if (config.getSslContext() != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Using custom SSLContext for connecting to: " + config.getBaseUrl());
			}
			httpClientBuilder.setSslcontext(config.getSslContext());
		}

		if (config.getHostnameVerifier() != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Using custom X509HostnameVerifier for connecting to: " + config.getBaseUrl());
			}
			httpClientBuilder.setHostnameVerifier(config.getHostnameVerifier());
		}

		return httpClientBuilder;
	}

	protected void configureSimpleSsl(HttpClientBuilder httpClientBuilder) {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) {
					return true;
				}
			}).build();
			httpClientBuilder.setSslcontext(sslContext);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to configure simple SSLContext, cause: " + ex.getMessage(), ex);
		}

		httpClientBuilder.setHostnameVerifier(new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) {
			}

			@Override
			public void verify(String host, X509Certificate cert) {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) {
			}

			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		});
	}
}
