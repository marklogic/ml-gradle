package com.marklogic.rest.util;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.security.cert.X509Certificate;

public class RestTemplateUtil {

	private final static Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

	public static RestTemplate newRestTemplate(String host, int port, String username, String password) {
		return newRestTemplate(new RestConfig(host, port, username, password));
	}

	public static RestTemplate newRestTemplate(RestConfig config) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		String username = config.getUsername();
		if (username != null) {
			BasicCredentialsProvider prov = new BasicCredentialsProvider();
			prov.setCredentials(new AuthScope(config.getHost(), config.getPort(), AuthScope.ANY_REALM),
				new UsernamePasswordCredentials(username, config.getPassword()));
			httpClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(prov);
		}

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

		HttpClient client = httpClientBuilder.build();

		RestTemplate rt = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
		rt.setErrorHandler(new MgmtResponseErrorHandler());
		return rt;
	}

	private static void configureSimpleSsl(HttpClientBuilder httpClientBuilder) {
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
				return false;
			}
		});
	}
}
