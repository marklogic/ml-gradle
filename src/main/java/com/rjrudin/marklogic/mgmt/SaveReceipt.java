package com.rjrudin.marklogic.mgmt;

import org.springframework.http.ResponseEntity;

public class SaveReceipt {

    private String resourceId;
    private String payload;
    private ResponseEntity<String> response;
    private String path;

    public SaveReceipt(String resourceId, String payload, String path, ResponseEntity<String> response) {
        super();
        this.resourceId = resourceId;
        this.payload = payload;
        this.path = path;
        this.response = response;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getPath() {
        return path;
    }

    public ResponseEntity<String> getResponse() {
        return response;
    }

    public String getPayload() {
        return payload;
    }
}
