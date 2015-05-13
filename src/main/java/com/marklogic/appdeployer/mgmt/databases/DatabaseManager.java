package com.marklogic.appdeployer.mgmt.databases;

import org.jdom2.Namespace;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.util.Fragment;
import com.marklogic.clientutil.LoggingObject;

public class DatabaseManager extends LoggingObject {

    private RestTemplate rt;
    private String baseUrl;

    public DatabaseManager(RestTemplate rt, String baseUrl) {
        this.rt = rt;
        this.baseUrl = baseUrl;
    }

    /**
     * The name is needed so we can easily determine if the database needs to be created or updated. The client should
     * have easy access to the name - probably from AppConfig - so that this class doesn't have to extract it from the
     * body.
     * 
     * @param name
     * @param body
     * @param format
     */
    public void createDatabase(String name, String body, String format) {
        if (dbExists(name)) {
            logger.warn("Database already exists: " + name);
        } else {
            logger.info("Creating database: " + name);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(body, headers);
            rt.exchange(baseUrl + "/manage/v2/databases?format=" + format, HttpMethod.POST, request, String.class);
            logger.info("Created database: " + name);
        }
    }

    public boolean dbExists(String name) {
        String xml = rt.getForObject(baseUrl + "/manage/v2/databases", String.class);
        Fragment f = new Fragment(xml, Namespace.getNamespace("db", "http://marklogic.com/manage/databases"));
        return f.elementExists(String.format("/db:database-default-list/db:list-items/db:list-item[db:nameref = '%s']",
                name));
    }

    public void setRt(RestTemplate rt) {
        this.rt = rt;
    }

    public void setBaseUrl(String baseUri) {
        this.baseUrl = baseUri;
    }
}
