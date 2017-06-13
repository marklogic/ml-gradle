package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.appservers.ServerManager;
import com.marklogic.mgmt.databases.DatabaseManager;
import com.marklogic.mgmt.security.PrivilegeManager;
import com.marklogic.mgmt.security.RoleManager;
import com.marklogic.mgmt.security.UserManager;
import com.marklogic.mgmt.tasks.TaskManager;
import com.marklogic.rest.util.ResourcesFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNameMatchingResourceSelector implements ResourceSelector {

	private List<String> includeTypes;

	protected abstract boolean nameMatches(String resourceName);

	@Override
	public ResourceSelection selectResources(ManageClient manageClient) {
		MapResourceSelection selection = new MapResourceSelection();

		select(selection, new ServerManager(manageClient), MapResourceSelection.SERVERS);
		select(selection, new DatabaseManager(manageClient), MapResourceSelection.DATABASES);
		select(selection, new RoleManager(manageClient), MapResourceSelection.ROLES);
		select(selection, new UserManager(manageClient), MapResourceSelection.USERS);
		selectPrivileges(selection, manageClient);
		selectTasks(selection, manageClient);

		return selection;
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
			ResourcesFragment databases = mgr.getAsXml();
			for (String name : databases.getListItemNameRefs()) {
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
}
