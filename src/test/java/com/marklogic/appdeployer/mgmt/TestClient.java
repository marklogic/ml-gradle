package com.marklogic.appdeployer.mgmt;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class TestClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8002;
        String username = "admin";
        String password = "admin";

        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username,
                password));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        HttpComponentsClientHttpRequestFactory f = new HttpComponentsClientHttpRequestFactory(client);
        RestTemplate rt = new RestTemplate(f);

        DatabaseManager dbMgr = new DatabaseManager(rt, "http://" + host + ":" + port);
        System.out.println(dbMgr.dbExists("sample-app-content"));

    }
}
