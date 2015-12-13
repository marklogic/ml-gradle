package com.rjrudin.marklogic.mgmt.api.database;

public class ElementIndex {

    private String scalarType;
    private String namespaceUri;
    private String localname;
    private String collation;
    private Boolean rangeValuePositions;
    private String invalidValues;

    public String getScalarType() {
        return scalarType;
    }

    public void setScalarType(String scalarType) {
        this.scalarType = scalarType;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    public String getLocalname() {
        return localname;
    }

    public void setLocalname(String localname) {
        this.localname = localname;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public Boolean isRangeValuePositions() {
        return rangeValuePositions;
    }

    public void setRangeValuePositions(Boolean rangeValuePositions) {
        this.rangeValuePositions = rangeValuePositions;
    }

    public String getInvalidValues() {
        return invalidValues;
    }

    public void setInvalidValues(String invalidValues) {
        this.invalidValues = invalidValues;
    }
}
