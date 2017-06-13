package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.security.RoleManager;

import java.util.ArrayList;
import java.util.List;

public class Role extends Resource {

    private String roleName;
    private String description;
    private String compartment;
    private List<String> externalName;
    private List<String> role;
    private List<Permission> permission;
    private List<RolePrivilege> privilege;
    private List<String> collection;

    public Role() {
    }

    public Role(API api, String roleName) {
        super(api);
        this.roleName = roleName;
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

    public void addPrivilege(RolePrivilege priv) {
        if (privilege == null) {
            privilege = new ArrayList<>();
        }
        privilege.add(priv);
    }

    public void addCollection(String c) {
        if (collection == null) {
            collection = new ArrayList<String>();
        }
        collection.add(c);
    }

    @Override
    protected ResourceManager getResourceManager() {
        return new RoleManager(getClient());
    }

    @Override
    protected String getResourceId() {
        return roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompartment() {
        return compartment;
    }

    public void setCompartment(String compartment) {
        this.compartment = compartment;
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

    public List<RolePrivilege> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<RolePrivilege> privilege) {
        this.privilege = privilege;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

}
