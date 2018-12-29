package com.marklogic.rest.util;

import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.RestTemplateUtil;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.security.cert.X509Certificate;

public class RestTemplateUtilTest extends AbstractMgmtTest {

	private boolean configurerInvoked = false;

	@Test
	public void configurerList() {
		assertEquals(4, RestTemplateUtil.DEFAULT_CONFIGURERS.size());

		assertFalse(configurerInvoked);
		RestTemplateUtil.DEFAULT_CONFIGURERS.add((restConfig, builder) -> {
			logger.info("Just a test of adding a configurer");
			configurerInvoked = true;
			return builder;
		});

		new ManageClient(manageConfig);
		assertTrue(configurerInvoked);
	}

	/**
	 * Just a smoke test to inspect the logging.
	 */
	@Test
	public void configureSimpleSsl() {
		manageConfig.setConfigureSimpleSsl(true);
		new ManageClient(manageConfig);
	}

	/**
	 * Another smoke test to inspect logging.
	 */
	@Test
	public void customSslContextAndHostnameVerifier() throws Exception {
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) {
				return true;
			}
		}).build();

		X509HostnameVerifier verifier = new X509HostnameVerifier() {
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
		};

		manageConfig.setSslContext(sslContext);
		manageConfig.setHostnameVerifier(verifier);

		new ManageClient(manageConfig);
	}
}
