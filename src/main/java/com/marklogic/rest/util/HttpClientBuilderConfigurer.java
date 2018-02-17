package com.marklogic.rest.util;

import org.apache.http.impl.client.HttpClientBuilder;

public interface HttpClientBuilderConfigurer {

	HttpClientBuilder configureHttpClientBuilder(RestConfig restConfig, HttpClientBuilder httpClientBuilder);

}
