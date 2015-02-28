package com.marklogic.appdeployer.ml7;

import java.io.FileReader;
import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.ManageClient;
import com.marklogic.clientutil.LoggingObject;

public class Ml7ManageClient extends LoggingObject implements ManageClient {

    private RestTemplate restTemplate;
    private String baseUri;

    public Ml7ManageClient(String host, int port, String username, String password) {
        super();

        this.baseUri = "http://" + host + ":" + port;
        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username,
                password));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        HttpComponentsClientHttpRequestFactory f = new HttpComponentsClientHttpRequestFactory(client);
        this.restTemplate = new RestTemplate(f);
    }

    @Override
    public void deletePackage(String name) {
        logger.info("Deleting package if it exists: " + name);
        restTemplate.delete(buildUri("/manage/v2/packages/" + name));
    }

    @Override
    public void createPackage(String name) {
        logger.info("Creating package: " + name);
        try {
            restTemplate.getForEntity(buildUri("/manage/v2/packages/" + name), String.class);
            logger.info("Package already exists");
        } catch (Exception e) {
            restTemplate.postForLocation(buildUri("/manage/v2/packages?pkgname=" + name), null);
        }
    }

    @Override
    public void installPackage(String name) {
        logger.info("Installing package: " + name);
        restTemplate.postForLocation(buildUri("/manage/v2/packages/" + name + "/install"), null);
    }

    protected String buildUri(String path) {
        return baseUri + path;
    }

    // TODO Move the XML replace stuff out of here
    @Override
    public void addDatabase(String packageName, String databaseName, String packageFilePath) {
        try {
            String xml = FileCopyUtils.copyToString(new FileReader(packageFilePath));
            xml = xml.replace("%%DATABASE_NAME%%", databaseName);
            logger.info("Adding database " + databaseName + " to package " + packageName);
            postXml("/manage/v2/packages/" + packageName + "/databases/" + databaseName, xml);
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
    }

    protected void postXml(String path, String xml) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<String>(xml, headers);
        restTemplate.postForLocation(buildUri(path), entity);
    }

    protected void postJson(String path, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        restTemplate.postForLocation(buildUri(path), entity);
    }

    @Override
    public void createRestApiServer(String serverName, String database, Integer port, String modulesDatabase) {
        if (!restApiServerExists(serverName)) {
            String json = "{rest-api: {name:\"" + serverName + "\"";
            if (database != null) {
                json += ", database: \"" + database + "\"";
            }
            if (port != null) {
                json += ", port: " + port;
            }
            if (modulesDatabase != null) {
                json += ", modulesDatabase: \"" + modulesDatabase + "\"";
            }
            json += "}}";
            logger.info("Creating new REST API server: " + json);
            postJson("/v1/rest-apis", json);
        } else {
            logger.info("REST API instance already exists with name: " + serverName);
        }
    }

    /**
     * TODO Rewrite this to get JSON or XML, parse it, and check for the server name.
     */
    public boolean restApiServerExists(String serverName) {
        String xml = restTemplate.getForEntity(buildUri("/v1/rest-apis/"), String.class).getBody();
        return xml.contains("<rapi:name>" + serverName + "</rapi:name>");
    }

    @Override
    public void addServer(String packageName, String serverName, String group, String packageXml) {
        postXml("/manage/v2/packages/" + packageName + "/servers/" + serverName + "?group-id=" + group, packageXml);
    }

}
