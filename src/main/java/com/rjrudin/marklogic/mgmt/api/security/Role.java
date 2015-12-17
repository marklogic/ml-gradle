package com.rjrudin.marklogic.mgmt.api.security;

import java.util.List;

import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.api.API;
import com.rjrudin.marklogic.mgmt.api.Resource;
import com.rjrudin.marklogic.mgmt.security.RoleManager;

public class Role extends Resource {

    private String roleName;
    private String description;
    private String compartment;
    private List<String> externalName;
    private List<String> role;
    private List<Permission> permission;
    private List<Privilege> privilege;
    private List<String> collection;

    public Role() {
    }

    public Role(API api, String roleName) {
        super(api);
        this.roleName = roleName;
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

    public List<Privilege> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<Privilege> privilege) {
        this.privilege = privilege;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

}
