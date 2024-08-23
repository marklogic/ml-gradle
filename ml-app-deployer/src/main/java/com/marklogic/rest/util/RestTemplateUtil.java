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

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.extra.okhttpclient.OkHttpClientBuilderFactory;
import com.marklogic.rest.util.configurer.BasicAuthConfigurer;
import com.marklogic.rest.util.configurer.NoConnectionReuseConfigurer;
import com.marklogic.rest.util.configurer.SslConfigurer;
import com.marklogic.rest.util.configurer.UseSystemPropertiesConfigurer;
import okhttp3.OkHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for constructing a Spring RestTemplate for communicating with the MarkLogic Manage API.
 * <p>
 * This class uses an org.apache.http.impl.client.HttpClientBuilder to construct an
 * org.apache.http.client.HttpClient that is then used to construct a RestTemplate. Instances of the interface
 * HttpClientBuilderConfigurer can be passed in to customize how the HttpClient is constructed. If no instances are passed
 * in, then the configurers defined by DEFAULT_CONFIGURERS are used.
 * </p>
 * <p>
 * The DEFAULT_CONFIGURERS variable is public and static but not final so that clients of ml-app-deployer can
 * fiddle with it to customize how classes within ml-app-deployer construct a RestTemplate.
 * </p>
 */
public class RestTemplateUtil {

	private final static Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

	/**
	 * @deprecated since 4.5.0; OkHttp is now the preferred client
	 */
	@Deprecated
	public static List<HttpClientBuilderConfigurer> DEFAULT_CONFIGURERS = new ArrayList<>();

	static {
		DEFAULT_CONFIGURERS.add(new BasicAuthConfigurer());
		DEFAULT_CONFIGURERS.add(new SslConfigurer());
		DEFAULT_CONFIGURERS.add(new NoConnectionReuseConfigurer());
		DEFAULT_CONFIGURERS.add(new UseSystemPropertiesConfigurer());
	}

	/**
	 * @deprecated since 4.5.0; OkHttp is now the preferred client
	 */
	@Deprecated
	public static RestTemplate newRestTemplate(String host, int port, String username, String password) {
		return newRestTemplate(new RestConfig(host, port, username, password));
	}

	/**
	 * As of 4.5.0, use this method for constructing a {@code RestTemplate} that supports all authentication types
	 * supported by MarkLogic. ml-app-deployer is expected to use this for all calls as well starting in 4.5.0.
	 *
	 * @param config
	 * @return
	 */
	public static RestTemplate newRestTemplate(RestConfig config) {
		OkHttpClient client;
		try {
			DatabaseClientFactory.Bean bean = config.newDatabaseClientBuilder().buildBean();
			client = OkHttpClientBuilderFactory
				.newOkHttpClientBuilder(bean.getHost(), bean.getSecurityContext())
				.build();
		} catch (RuntimeException ex) {
			throw new RuntimeException(String.format("Unable to connect to the MarkLogic app server at %s; cause: %s", config.toString(), ex.getMessage()));
		}

		RestTemplate rt = new RestTemplate(new OkHttp3ClientHttpRequestFactory(client));
		rt.setErrorHandler(new MgmtResponseErrorHandler());
		return rt;
	}

	/**
	 * @deprecated since 4.5.0; OkHttp is now the preferred client
	 */
	@Deprecated
	public static RestTemplate newRestTemplate(String host, int port, String username, String password, HttpClientBuilderConfigurer... configurers) {
		return newRestTemplate(new RestConfig(host, port, username, password), configurers);
	}

	/**
	 * @deprecated since 4.5.0; OkHttp is now the preferred client
	 */
	@Deprecated
	public static RestTemplate newRestTemplate(RestConfig config, List<HttpClientBuilderConfigurer> configurers) {
		return newRestTemplate(config, configurers.toArray(new HttpClientBuilderConfigurer[]{}));
	}

	/**
	 * @deprecated since 4.5.0; OkHttp is now the preferred client
	 */
	@Deprecated
	public static RestTemplate newRestTemplate(RestConfig config, HttpClientBuilderConfigurer... configurers) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		if (configurers != null) {
			for (HttpClientBuilderConfigurer configurer : configurers) {
				if (logger.isDebugEnabled()) {
					logger.debug("Applying HttpClientBuilderConfigurer: " + configurer);
				}
				httpClientBuilder = configurer.configureHttpClientBuilder(config, httpClientBuilder);
			}
		}

		HttpClient client = httpClientBuilder.build();
		RestTemplate rt = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
		rt.setErrorHandler(new MgmtResponseErrorHandler());
		return rt;
	}

}
