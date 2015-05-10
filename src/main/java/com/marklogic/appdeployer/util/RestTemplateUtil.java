package com.marklogic.appdeployer.util;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.mgmt.ManageConfig;

public class RestTemplateUtil {

    /**
     * Convenience method for creating a RestTemplate with basic auth configured.
     */
    public static RestTemplate newRestTemplate(ManageConfig config) {
        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(config.getHost(), config.getPort(), AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }
}
