package com.rjrudin.marklogic.mgmt.api.security;

import java.util.ArrayList;
import java.util.List;

import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.api.API;
import com.rjrudin.marklogic.mgmt.api.Resource;
import com.rjrudin.marklogic.mgmt.security.UserManager;

public class User extends Resource {

    private String userName;
    private String description;
    private String password;
    private List<String> externalName;
    private List<String> role;
    private List<Permission> permission;
    private List<String> collection;

    public User() {
        super();
    }

    public User(API api, String userName) {
        super(api);
        this.userName = userName;
    }

    @Override
    protected ResourceManager getResourceManager() {
        return new UserManager(getClient());
    }

    @Override
    protected String getResourceId() {
        return userName;
    }

    public void addExternalName(String name) {
        if (externalName == null) {
            externalName = new ArrayList<>();
        }
        externalName.add(name);
    }

    public void addRole(String r) {
        if (role == null) {
            role = new ArrayList<String>();
        }
        role.add(r);
    }

    public void addPermission(Permission p) {
        if (permission == null) {
            permission = new ArrayList<>();
        }
        permission.add(p);
    }

    public void addCollection(String c) {
        if (collection == null) {
            collection = new ArrayList<String>();
        }
        collection.add(c);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getExternalName() {
        return externalName;
    }

    public void setExternalName(List<String> externalName) {
        this.externalName = externalName;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public List<Permission> getPermission() {
        return permission;
    }

    public void setPermission(List<Permission> permission) {
        this.permission = permission;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

}
