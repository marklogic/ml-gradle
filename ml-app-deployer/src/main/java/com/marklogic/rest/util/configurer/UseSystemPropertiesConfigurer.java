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
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @deprecated since 4.5.0; OkHttp is now the preferred client
 */
@Deprecated
public class UseSystemPropertiesConfigurer implements HttpClientBuilderConfigurer {

	@Override
	public HttpClientBuilder configureHttpClientBuilder(RestConfig restConfig, HttpClientBuilder httpClientBuilder) {
		return httpClientBuilder.useSystemProperties();
	}

}
