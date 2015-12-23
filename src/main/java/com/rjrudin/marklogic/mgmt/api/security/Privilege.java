package com.rjrudin.marklogic.mgmt.api.security;

import java.util.ArrayList;
import java.util.List;

import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.api.API;
import com.rjrudin.marklogic.mgmt.api.Resource;
import com.rjrudin.marklogic.mgmt.security.PrivilegeManager;

public class Privilege extends Resource {

    private String privilegeName;
    private String action;
    private String kind;
    private List<String> role;

    public Privilege() {
        super();
    }

    public Privilege(API api, String privilegeName) {
        super(api);
        this.privilegeName = privilegeName;
    }

    public void addRole(String r) {
        if (role == null) {
            role = new ArrayList<String>();
        }
        role.add(r);
    }

    @Override
    protected ResourceManager getResourceManager() {
        return new PrivilegeManager(getClient());
    }

    @Override
    protected String getResourceId() {
        return privilegeName;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

}
