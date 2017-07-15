package com.marklogic.mgmt.api.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "role-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Role extends Resource {

	@JsonProperty("role-name")
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

	private static JAXBContext roleJaxbContext;

	public Role() {
	}

	public Role(API api, String roleName) {
		super(api);
		this.roleName = roleName;
	}

	/**
	 * This is a temporary home for this method; it will be moved to something more reusable in version 3.x.
	 *
	 * @param xml
	 * @return
	 */
	public static Role parseXml(String xml) {
		try {
			if (roleJaxbContext == null) {
				roleJaxbContext = JAXBContext.newInstance(Role.class);
			}
			return (Role) roleJaxbContext.createUnmarshaller().unmarshal(new StringReader(xml));
		} catch (Exception ex) {
			throw new RuntimeException("Unable to parse XML for role, cause: " + ex.getMessage(), ex);
		}
	}

	/**
	 * This is a temporary home for this method; it will be moved to something more reusable in version 3.x.
	 *
	 * @param xml
	 * @return
	 */
	public static Role parseJson(API api, String json) {
		try {
			Role role = api.getObjectMapper().readerFor(Role.class).readValue(json);
			role.setObjectMapper(api.getObjectMapper());
			return role;
		} catch (Exception ex) {
			throw new RuntimeException("Unable to parse JSON for role, cause: " + ex.getMessage(), ex);
		}
	}

	public boolean hasPermissionWithOwnRoleName() {
		if (permission != null && roleName != null) {
			for (Permission perm : permission) {
				if (roleName.equals(perm.getRoleName())) {
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
