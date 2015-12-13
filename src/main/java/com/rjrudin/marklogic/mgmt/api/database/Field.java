package com.rjrudin.marklogic.mgmt.api.database;

import java.util.List;

public class Field {

    private String fieldName;
    private Boolean includeRoot;
    private List<ExcludedElement> excludedElement;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getIncludeRoot() {
        return includeRoot;
    }

    public void setIncludeRoot(Boolean includeRoot) {
        this.includeRoot = includeRoot;
    }

    public List<ExcludedElement> getExcludedElement() {
        return excludedElement;
    }

    public void setExcludedElement(List<ExcludedElement> excludedElement) {
        this.excludedElement = excludedElement;
    }
}
