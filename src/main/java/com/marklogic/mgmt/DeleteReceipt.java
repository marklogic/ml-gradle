package com.marklogic.mgmt;

public class DeleteReceipt {

    private String resourceId;
    private String path;
    private boolean deleted;

    public DeleteReceipt(String resourceId, String path, boolean deleted) {
        this.resourceId = resourceId;
        this.path = path;
        this.deleted = deleted;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getPath() {
        return path;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
