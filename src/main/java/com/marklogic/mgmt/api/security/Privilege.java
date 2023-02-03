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

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;

import javax.xml.bind.annotation.*;
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
