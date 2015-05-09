package com.marklogic.appdeployer.mgmt;

import org.jdom2.Namespace;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.util.Fragment;
import com.marklogic.clientutil.LoggingObject;

public class DatabaseManager extends LoggingObject {

    private RestTemplate rt;
    private String baseUri;

    public DatabaseManager(RestTemplate rt, String baseUri) {
        this.rt = rt;
        this.baseUri = baseUri;
    }

    public boolean dbExists(String name) {
        String xml = rt.getForObject(baseUri + "/manage/v2/databases", String.class);
        Fragment f = new Fragment(xml, Namespace.getNamespace("db", "http://marklogic.com/manage/databases"));
        f.prettyPrint();
        return f.elementExists(String.format("/db:database-default-list/db:list-items/db:list-item[db:nameref = '%s']",
                name));
    }

    public void setRt(RestTemplate rt) {
        this.rt = rt;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
