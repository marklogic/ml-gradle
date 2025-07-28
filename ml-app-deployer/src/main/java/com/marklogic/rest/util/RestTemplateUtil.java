/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.extra.okhttpclient.OkHttpClientBuilderFactory;
import okhttp3.OkHttpClient;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Factory class for constructing a Spring RestTemplate for communicating with the MarkLogic Manage API.
 */
public class RestTemplateUtil {

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

			OkHttpClient.Builder builder = OkHttpClientBuilderFactory
				.newOkHttpClientBuilder(bean.getHost(), bean.getSecurityContext());

			if (config.getClientConfigurator() != null) {
				config.getClientConfigurator().configure(builder);
			}

			client = builder.build();
		} catch (RuntimeException ex) {
			throw new RuntimeException(String.format("Unable to connect to the MarkLogic app server at %s; cause: %s", config.toString(), ex.getMessage()));
		}

		RestTemplate rt = new RestTemplate(new OkHttp3ClientHttpRequestFactory(client));
		rt.setErrorHandler(new MgmtResponseErrorHandler());
		return rt;
	}
}
