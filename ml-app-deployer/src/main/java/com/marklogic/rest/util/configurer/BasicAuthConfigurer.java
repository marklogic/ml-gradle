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
package com.marklogic.rest.util.configurer;

import com.marklogic.rest.util.HttpClientBuilderConfigurer;
import com.marklogic.rest.util.RestConfig;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @deprecated since 4.5.0; OkHttp is now the preferred client
 */
@Deprecated
public class BasicAuthConfigurer implements HttpClientBuilderConfigurer {

	@Override
	public HttpClientBuilder configureHttpClientBuilder(RestConfig config, HttpClientBuilder httpClientBuilder) {
		String username = config.getUsername();
		if (username != null) {
			BasicCredentialsProvider prov = new BasicCredentialsProvider();
			prov.setCredentials(new AuthScope(config.getHost(), config.getPort(), AuthScope.ANY_REALM),
				new UsernamePasswordCredentials(username, config.getPassword()));
			return httpClientBuilder.setDefaultCredentialsProvider(prov);
		}
		return httpClientBuilder;
	}
}
