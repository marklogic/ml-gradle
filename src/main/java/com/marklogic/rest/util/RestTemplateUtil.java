package com.marklogic.rest.util;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class RestTemplateUtil {

	public static RestTemplate newRestTemplate(RestConfig config) {
		return newRestTemplate(config.getHost(), config.getPort(), config.getUsername(), config.getPassword(), config.isConfigureSimpleSsl());
	}

	public static RestTemplate newRestTemplate(String host, int port, String username, String password) {
		return newRestTemplate(host, port, username, password, false);
	}

	/**
	 *
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param configureSimpleSsl if true, then a very simple SSLContext that trusts every request will be added to the
	 *                           HttpClient that RestTemplate uses
	 * @return
	 */
	public static RestTemplate newRestTemplate(String host, int port, String username, String password, boolean configureSimpleSsl) {
		BasicCredentialsProvider prov = new BasicCredentialsProvider();
		prov.setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username,
			password));

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultCredentialsProvider(prov);

		if (configureSimpleSsl) {
			try {
				SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
				httpClientBuilder.setSslcontext(sslContext);
				httpClientBuilder.setHostnameVerifier(new X509HostnameVerifier() {
					@Override
					public void verify(String host, SSLSocket ssl) throws IOException {}

					@Override
					public void verify(String host, X509Certificate cert) throws SSLException {}

					@Override
					public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {}

					@Override
					public boolean verify(String s, SSLSession sslSession) {
						return false;
					}
				});
			} catch (Exception ex) {
				throw new RuntimeException("Unable to configure simple SSL approach: " + ex.getMessage(), ex);
			}
		}

		HttpClient client = httpClientBuilder.build();

		RestTemplate rt = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
		rt.setErrorHandler(new MgmtResponseErrorHandler());
		return rt;
	}
}
