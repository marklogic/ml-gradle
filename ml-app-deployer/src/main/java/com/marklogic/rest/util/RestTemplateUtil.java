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
}
