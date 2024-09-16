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
package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.cpf.CpfConfigManager;
import com.marklogic.mgmt.resource.cpf.DomainManager;
import com.marklogic.mgmt.resource.cpf.PipelineManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.groups.GroupManager;
import com.marklogic.mgmt.resource.security.AmpManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.resource.security.UserManager;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import com.marklogic.mgmt.resource.triggers.TriggerManager;
import com.marklogic.rest.util.ResourcesFragment;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNameMatchingResourceSelector implements ResourceSelector {

	private List<String> includeTypes;
	private String triggersDatabase;

	protected abstract boolean nameMatches(String resourceName);

	@Override
	public ResourceSelection selectResources(ManageClient manageClient) {
		MapResourceSelection selection = new MapResourceSelection();

		select(selection, new ServerManager(manageClient), MapResourceSelection.SERVERS);
		select(selection, new DatabaseManager(manageClient), MapResourceSelection.DATABASES);
		select(selection, new RoleManager(manageClient), MapResourceSelection.ROLES);
		select(selection, new UserManager(manageClient), MapResourceSelection.USERS);
		select(selection, new GroupManager(manageClient), MapResourceSelection.GROUPS);

		if (triggersDatabase != null) {
			select(selection, new CpfConfigManager(manageClient, triggersDatabase), MapResourceSelection.CPF_CONFIGS);
			select(selection, new DomainManager(manageClient, triggersDatabase), MapResourceSelection.DOMAINS);
			select(selection, new PipelineManager(manageClient, triggersDatabase), MapResourceSelection.PIPELINES);
			select(selection, new TriggerManager(manageClient, triggersDatabase), MapResourceSelection.TRIGGERS);
		}

		selectPrivileges(selection, manageClient);
		selectTasks(selection, manageClient);
		selectAmps(selection, manageClient);

		return selection;
	}

	protected void selectAmps(MapResourceSelection selection, ManageClient manageClient) {
		if (includeTypes == null || includeTypes.contains(MapResourceSelection.AMPS)) {
			AmpManager mgr = new AmpManager(manageClient);
			ResourcesFragment amps = mgr.getAsXml();
			Namespace ns = Namespace.getNamespace("http://marklogic.com/manage/security");
			for (Element amp : amps.getListItems()) {
				String nameref = amp.getChildText("nameref", ns);
				if (nameMatches(nameref)) {
					String uriref = amp.getChildText("uriref", ns);
					selection.select(MapResourceSelection.AMPS, uriref);
				}
			}
		}
	}

	protected void selectTasks(MapResourceSelection selection, ManageClient manageClient) {
		if (includeTypes == null || includeTypes.contains(MapResourceSelection.TASKS)) {
			TaskManager taskManager = new TaskManager(manageClient);
			for (String path : taskManager.getTaskPaths()) {
				if (nameMatches(path)) {
					selection.select(MapResourceSelection.TASKS, path);
				}
			}
		}
	}

	protected void selectPrivileges(MapResourceSelection selection, ManageClient manageClient) {
		if (includeTypes == null || includeTypes.contains(MapResourceSelection.PRIVILEGES_EXECUTE) || includeTypes.contains(MapResourceSelection.PRIVILEGES_URI)) {
			PrivilegeManager privilegeManager = new PrivilegeManager(manageClient);
			ResourcesFragment privileges = privilegeManager.getAsXml();
			for (String name : privileges.getListItemNameRefs()) {
				if (nameMatches(name)) {
					String kind = privileges.getListItemValue(name, "kind");
					if ("uri".equals(kind)) {
						selection.select(MapResourceSelection.PRIVILEGES_URI, name);
					} else {
						selection.select(MapResourceSelection.PRIVILEGES_EXECUTE, name);
					}
				}
			}
		}
	}

	protected void select(MapResourceSelection selection, ResourceManager mgr, String type) {
		if (includeTypes == null || includeTypes.contains(type)) {
			ResourcesFragment resources = mgr.getAsXml();
			for (String name : resources.getListItemNameRefs()) {
				if (nameMatches(name)) {
					selection.select(type, name);
				}
			}
		}
	}

	public void setIncludeTypesAsString(String commaDelimitedTypes) {
		if (commaDelimitedTypes != null) {
			setIncludeTypes(commaDelimitedTypes.split(","));
		}
	}

	public void setIncludeTypes(String... types) {
		this.includeTypes = new ArrayList<>();
		for (String type : types) {
			// Make it easy to specify the two types of privileges
			if ("privileges".equals(type)) {
				this.includeTypes.add(MapResourceSelection.PRIVILEGES_EXECUTE);
				this.includeTypes.add(MapResourceSelection.PRIVILEGES_URI);
			} else {
				this.includeTypes.add(type);
			}
		}
	}

	public String getTriggersDatabase() {
		return triggersDatabase;
	}

	public void setTriggersDatabase(String triggersDatabase) {
		this.triggersDatabase = triggersDatabase;
	}
}
