/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.api.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "role-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Role extends Resource {

	@XmlElement(name = "role-name")
	private String roleName;

	private String description;
	private String compartment;

	@XmlElementWrapper(name = "external-names")
	@XmlElement(name = "external-name")
	private List<String> externalName;

	@XmlElementWrapper(name = "roles")
	private List<String> role;

	@XmlElementWrapper(name = "permissions")
	private List<Permission> permission;

	@XmlElementWrapper(name = "privileges")
	private List<RolePrivilege> privilege;

	@XmlElementWrapper(name = "collections")
	private List<String> collection;

	// This does not yet have an XmlElementWrapper on it as CapabilityQuery does not yet support XML marshalling;
	// unclear how to use e.g. XmlAnyElement on the query portion of it
	private List<CapabilityQuery> capabilityQuery;

	public Role() {
	}

	public Role(String roleName) {
		this(null, roleName);
	}

	public Role(API api, String roleName) {
		super(api);
		this.roleName = roleName;
	}

	public boolean hasPermissionsOrRoles() {
		return (role != null && !role.isEmpty()) || (permission != null && !permission.isEmpty());
	}

	@JsonIgnore
	public List<String> getDependentRoleNames() {
		List<String> names = new ArrayList<>();
		if (role != null) {
			names.addAll(role);
		}
		if (permission != null) {
			permission.forEach(p -> {
				if (!names.contains(p.getRoleName())) {
					names.add(p.getRoleName());
				}
			});
		}
		return names;
	}

	public void clearPermissionsAndRoles() {
		if (role != null) {
			role.clear();
			role = null;
		}
		if (permission != null) {
			permission.clear();
			permission = null;
		}
	}

	public boolean hasPermissionWithOwnRoleName() {
		return hasPermissionWithRoleName(this.roleName);
	}

	public boolean hasPermissionWithRoleName(String someRoleName) {
		if (permission != null && someRoleName != null) {
			for (Permission perm : permission) {
				if (someRoleName.equals(perm.getRoleName())) {
					return true;
				}
			}
		}
		return false;
	}

	public void addExternalName(String name) {
		if (externalName == null) {
			externalName = new ArrayList<>();
		}
		externalName.add(name);
	}

	public void addRole(String r) {
		if (role == null) {
			role = new ArrayList<>();
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
			collection = new ArrayList<>();
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

	public List<CapabilityQuery> getCapabilityQuery() {
		return capabilityQuery;
	}

	public void setCapabilityQuery(List<CapabilityQuery> capabilityQuery) {
		this.capabilityQuery = capabilityQuery;
	}
}
