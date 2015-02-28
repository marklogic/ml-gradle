package com.marklogic.appdeployer.ml7;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.ManageConfig;
import com.marklogic.clientutil.LoggingObject;

public class Ml7AppDeployer extends LoggingObject implements AppDeployer {

    private AppConfig appConfig;
    private ManageConfig manageConfig;
    private RestTemplate restTemplate;

    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        appConfig.setName("appdeployer");
        Ml7AppDeployer sut = new Ml7AppDeployer(appConfig);
        sut.deletePackage();
        sut.createPackage();
    }

    public Ml7AppDeployer(AppConfig appConfig) {
        this(appConfig, new ManageConfig());
    }

    public Ml7AppDeployer(AppConfig appConfig, ManageConfig manageConfig) {
        this.appConfig = appConfig;
        this.manageConfig = manageConfig;
        this.restTemplate = buildRestTemplate(manageConfig);
    }

    @Override
    public void deletePackage() {
        logger.info("Deleting package if it exists: " + getPackageName());
        restTemplate.delete(buildUri("/manage/v2/packages/" + getPackageName()));
    }

    @Override
    public void createPackage() {
        String name = getPackageName();
        logger.info("Creating package: " + name);
        try {
            restTemplate.getForEntity(buildUri("/manage/v2/packages/" + name), String.class);
            logger.info("Package already exists");
        } catch (Exception e) {
            restTemplate.postForLocation(buildUri("/manage/v2/packages?pkgname=" + name), null);
        }
    }

    @Override
    public void installPackage() {
        String name = getPackageName();
        logger.info("Installing package: " + name);
        restTemplate.postForLocation(buildUri("/manage/v2/packages/" + name + "/install"), null);
    }

    protected RestTemplate buildRestTemplate(ManageConfig manageConfig) {
        BasicCredentialsProvider prov = new BasicCredentialsProvider();
        prov.setCredentials(new AuthScope(manageConfig.getHost(), manageConfig.getPort(), AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(manageConfig.getUsername(), manageConfig.getPassword()));
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(prov).build();
        HttpComponentsClientHttpRequestFactory f = new HttpComponentsClientHttpRequestFactory(client);
        return new RestTemplate(f);
    }

    protected String buildUri(String path) {
        return manageConfig.getUri() + path;
    }

    protected String getPackageName() {
        return appConfig.getName() + "-package";
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public ManageConfig getManageConfig() {
        return manageConfig;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
