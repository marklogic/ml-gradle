package com.marklogic.appdeployer.ml7;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
        String xml = restTemplate.getForEntity(buildUri("/manage/v2/packages"), String.class).getBody();
        if (xml.contains(name)) {
            String msg = String.format("package %s", name);
            logger.info("Deleting " + msg);
            restTemplate.delete(buildUri("/manage/v2/packages/" + name));
            logger.info("Finished deleting " + msg);
        }
    }

    /**
     * This used to check for the existence of the package first, but that results in an ugly Spring RestTemplate WARN
     * statement that could confuse users. It's up to the caller to verify that the package doesn't exist yet (i.e. call
     * deletePackage first).
     */
    @Override
    public void createPackage(String name) {
        String msg = String.format("package %s", name);
        logger.info("Creating " + msg);
        restTemplate.postForLocation(buildUri("/manage/v2/packages?pkgname=" + name), null);
        logger.info("Finished creating " + msg);
    }

    @Override
    public void installPackage(String name) {
        String msg = String.format("package %s", name);
        logger.info("Installing " + msg);
        restTemplate.postForLocation(buildUri("/manage/v2/packages/" + name + "/install"), null);
        logger.info("Finished installing " + msg);
    }

    protected String buildUri(String path) {
        return baseUri + path;
    }

    @Override
    public void addDatabase(String packageName, String databaseName, String packageXml) {
        String msg = String.format("database %s to package %s", databaseName, packageName);
        logger.info("Adding " + msg);
        postXml("/manage/v2/packages/" + packageName + "/databases/" + databaseName, packageXml);
        logger.info("Finished adding " + msg);
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
            logger.info("Finished creating REST API server");
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
        String msg = String.format("server %s to package %s", serverName, packageName);
        logger.info("Adding " + msg);
        postXml("/manage/v2/packages/" + packageName + "/servers/" + serverName + "?group-id=" + group, packageXml);
        logger.info("Finished adding " + msg);
    }

    public boolean xdbcServerExists(String serverName, String groupName) {
        String path = "/manage/v2/servers/" + serverName + "?group-id=" + groupName;
        try {
            restTemplate.getForEntity(buildUri(path), String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
