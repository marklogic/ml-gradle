/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "privilege-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Privilege extends Resource {

	@XmlElement(name = "privilege-name")
	private String privilegeName;
	private String action;
	private String kind;

	@XmlElementWrapper(name = "roles")
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
			role = new ArrayList<>();
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
