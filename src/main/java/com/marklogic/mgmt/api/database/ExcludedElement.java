package com.marklogic.mgmt.api.database;

public class ExcludedElement extends Element {

    private String attributeNamespaceUri;
    private String attributeLocalname;
    private String attributeValue;

    public String getAttributeNamespaceUri() {
        return attributeNamespaceUri;
    }

    public void setAttributeNamespaceUri(String attributeNamespaceUri) {
        this.attributeNamespaceUri = attributeNamespaceUri;
    }

    public String getAttributeLocalname() {
        return attributeLocalname;
    }

    public void setAttributeLocalname(String attributeLocalname) {
        this.attributeLocalname = attributeLocalname;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
