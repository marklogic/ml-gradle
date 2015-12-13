package com.rjrudin.marklogic.mgmt.api.forest;

import com.rjrudin.marklogic.mgmt.api.ApiObject;

public class Range extends ApiObject {

    private String lowerBound;
    private String upperBound;

    public String getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }
}
