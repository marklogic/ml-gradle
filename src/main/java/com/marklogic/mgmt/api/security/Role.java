package com.marklogic.mgmt.api.security;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@XmlRootElement(name = "role-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Role extends Resource implements Comparable<Role> {

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

	public Role() {
	}

	public Role(String roleName) {
		this(null, roleName);
	}

	public Role(API api, String roleName) {
		super(api);
		this.roleName = roleName;
	}

	@Override
	public int compareTo(Role other) {
		if (other == null || other.getRoleName() == null) {
			return 1;
		}
		if (this.roleName == null) {
			return -1;
		}

		final String otherName = other.getRoleName();
		if (otherName == null) {
			return 1;
		}

		if (this.role != null && this.role.contains(otherName)) {
			return 1;
		}
		if (hasPermissionWithRoleName(otherName)) {
			return 1;
		}

		if (other.getRole() != null && other.getRole().contains(this.roleName)) {
			return -1;
		}
		if (other.hasPermissionWithRoleName(this.roleName)) {
			return -1;
		}

		final boolean hasDependencies = hasPermissionsOrRoles();
		final boolean otherHasDependencies = other.hasPermissionsOrRoles();

		if (hasDependencies && otherHasDependencies) {
			return this.roleName.compareTo(otherName);
		}

		if (hasDependencies) {
			return 1;
		}

		if (otherHasDependencies) {
			return -1;
		}

		return this.roleName.compareTo(otherName);
	}

	/**
	 * Return a new list of object nodes, sorted based on the dependencies of each role.
	 *
	 * @param objectNodes
	 * @return
	 */
	public static List<ObjectNode> sortObjectNodes(List<ObjectNode> objectNodes) {
		List<Role> roles = new ArrayList<>();
		ObjectReader reader = ObjectMapperFactory.getObjectMapper().readerFor(Role.class);
		for (ObjectNode node : objectNodes) {
			try {
				roles.add(reader.readValue(node));
			} catch (IOException e) {
				throw new RuntimeException("Unable to read ObjectNode into Role; node: " + node, e);
			}
		}
		Collections.sort(roles, Comparator.naturalOrder());

		List<ObjectNode> newList = new ArrayList<>();
		roles.forEach(role -> newList.add(role.toObjectNode()));
		return newList;
	}

	public boolean hasPermissionsOrRoles() {
		return (role != null && !role.isEmpty()) || (permission != null && !permission.isEmpty());
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
