package com.marklogic.rest.util.configurer;

import com.marklogic.rest.util.HttpClientBuilderConfigurer;
import com.marklogic.rest.util.RestConfig;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

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
