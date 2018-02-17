package com.marklogic.rest.util.configurer;

import com.marklogic.rest.util.HttpClientBuilderConfigurer;
import com.marklogic.rest.util.RestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

public class UseSystemPropertiesConfigurer implements HttpClientBuilderConfigurer {

	@Override
	public HttpClientBuilder configureHttpClientBuilder(RestConfig restConfig, HttpClientBuilder httpClientBuilder) {
		return httpClientBuilder.useSystemProperties();
	}

}
