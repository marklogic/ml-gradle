package com.rjrudin.marklogic.mgmt.api.database;

import java.util.List;

public class Element {

    private String namespaceUri;
    private List<String> localname;

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    public List<String> getLocalname() {
        return localname;
    }

    public void setLocalname(List<String> localname) {
        this.localname = localname;
    }

}
