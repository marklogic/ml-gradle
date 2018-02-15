package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static void main(String[] args) {
		API api = new API(new ManageClient());
		Role r = new Role();
		r.setRoleName("hi");
		r.setObjectMapper(api.getObjectMapper());
		r.setExternalName(Arrays.asList("name1", "name2"));
		System.out.println(r.getJson());

	}

	public Role() {
	}

	public Role(API api, String roleName) {
		super(api);
		this.roleName = roleName;
	}

	public boolean hasPermissionsOrRoles() {
		return (role != null && !role.isEmpty()) || (permission != null && !permission.isEmpty());
	}

	public void clearPermissionsAndRoles() {
		if (role != null) {
			role.clear();
		}
		if (permission != null) {
			permission.clear();
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
