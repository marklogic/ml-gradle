package com.rjrudin.marklogic.mgmt.api.security;

import java.util.ArrayList;
import java.util.List;

import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.api.API;
import com.rjrudin.marklogic.mgmt.api.Resource;
import com.rjrudin.marklogic.mgmt.security.ProtectedCollectionsManager;

public class ProtectedCollection extends Resource {

    private String collection;
    private List<Permission> permission;

    public ProtectedCollection() {
        super();
    }

    public ProtectedCollection(API api, String collection) {
        super(api);
        this.collection = collection;
    }

    public void addPermission(Permission p) {
        if (permission == null) {
            permission = new ArrayList<>();
        }
        permission.add(p);
    }

    @Override
    protected ResourceManager getResourceManager() {
        return new ProtectedCollectionsManager(getClient());
    }

    @Override
    protected String getResourceId() {
        return collection;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public List<Permission> getPermission() {
        return permission;
    }

    public void setPermission(List<Permission> permission) {
        this.permission = permission;
    }

}
