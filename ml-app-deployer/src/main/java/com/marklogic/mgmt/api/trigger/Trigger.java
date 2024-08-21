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
package com.marklogic.mgmt.api.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.triggers.TriggerManager;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "trigger-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Trigger extends Resource {

	// This is not part of the payload, but is needed for constructing a TriggerManager
	@JsonIgnore
	@XmlTransient
	private String databaseName;

	private String id;
	private String name;
	private String description;
	private Event event;
	private String module;

	@XmlElement(name = "module-db")
	private String moduleDb;

	@XmlElement(name = "module-root")
	private String moduleRoot;

	private Boolean enabled;
	private Boolean recursive;

	@XmlElement(name = "task-priority")
	private String taskPriority;

	@XmlElementWrapper(name = "permissions")
	@XmlElement(name = "permission")
	private List<Permission> permission;

	@Override
	protected ResourceManager getResourceManager() {
		if (databaseName == null) {
			throw new IllegalStateException("Cannot construct TriggerManager because a databaseName has not been set");
		}
		return new TriggerManager(getClient(), databaseName);
	}

	@Override
	protected String getResourceId() {
		return name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModuleDb() {
		return moduleDb;
	}

	public void setModuleDb(String moduleDb) {
		this.moduleDb = moduleDb;
	}

	public String getModuleRoot() {
		return moduleRoot;
	}

	public void setModuleRoot(String moduleRoot) {
		this.moduleRoot = moduleRoot;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getRecursive() {
		return recursive;
	}

	public void setRecursive(Boolean recursive) {
		this.recursive = recursive;
	}

	public String getTaskPriority() {
		return taskPriority;
	}

	public void setTaskPriority(String taskPriority) {
		this.taskPriority = taskPriority;
	}

	public List<Permission> getPermission() {
		return permission;
	}

	public void setPermission(List<Permission> permission) {
		this.permission = permission;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}
