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
