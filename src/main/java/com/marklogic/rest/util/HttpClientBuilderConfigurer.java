package com.marklogic.rest.util;

import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @deprecated since 4.5.0; OkHttp is now the preferred client
 */
@Deprecated
public interface HttpClientBuilderConfigurer {

	HttpClientBuilder configureHttpClientBuilder(RestConfig restConfig, HttpClientBuilder httpClientBuilder);

}
