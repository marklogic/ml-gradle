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
package com.marklogic.mgmt.api.security.queryroleset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.QueryRolesetManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "query-roleset-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryRoleset extends Resource {

	@XmlElement(name = "role-id")
	private String roleId;

	@XmlElementWrapper(name = "query-roleset")
	@XmlElement(name = "role-name")
	private List<String> roleName;

	public QueryRoleset() {
	}

	public QueryRoleset(String roleId) {
		this.roleId = roleId;
	}

	@Override
	protected ResourceManager getResourceManager() {
		return new QueryRolesetManager(getClient());
	}

	/**
	 * Based on the design of QueryRolesetManager, the resource ID for a query roleset is a JSON array string of this
	 * roleset's roles.
	 *
	 * @return
	 */
	@Override
	protected String getResourceId() {
		return getRoleNamesAsJsonArrayString();
	}

	@JsonIgnore
	public String getRoleNamesAsJsonArrayString() {
		ArrayNode array = ObjectMapperFactory.getObjectMapper().createArrayNode();
		if (roleName != null) {
			roleName.forEach(role -> array.add(role));
		}
		return array.toString();
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public List<String> getRoleName() {
		return roleName;
	}

	public void setRoleName(List<String> roleName) {
		this.roleName = roleName;
	}
}
