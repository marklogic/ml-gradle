package com.marklogic.mgmt.selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapResourceSelection implements ResourceSelection {

	public final static String DATABASES = "databases";
	public final static String PRIVILEGES_EXECUTE = "privilegesExecute";
	public final static String PRIVILEGES_URI = "privilegesUri";
	public final static String ROLES = "roles";
	public final static String SERVERS = "servers";
	public final static String TASKS = "tasks";
	public final static String USERS = "users";

	private Map<String, List<String>> selections = new HashMap<>();

	public void select(String type, String name) {
		List<String> names = selections.get(type);
		if (names == null) {
			names = new ArrayList<>();
			selections.put(type, names);
		}
		names.add(name);
	}

	protected String[] getSelectedResourceNames(String type) {
		List<String> names = selections.get(type);
		return names != null ? names.toArray(new String[]{}) : new String[]{};
	}

	@Override
	public String[] getDatabaseNames() {
		return getSelectedResourceNames(DATABASES);
	}

	@Override
	public String[] getPrivilegeExecuteNames() {
		return getSelectedResourceNames(PRIVILEGES_EXECUTE);
	}

	@Override
	public String[] getPrivilegeUriNames() {
		return getSelectedResourceNames(PRIVILEGES_URI);
	}

	@Override
	public String[] getRoleNames() {
		return getSelectedResourceNames(ROLES);
	}

	@Override
	public String[] getServerNames() {
		return getSelectedResourceNames(SERVERS);
	}

	@Override
	public String[] getTaskNames() {
		return getSelectedResourceNames(TASKS);
	}

	@Override
	public String[] getUserNames() {
		return getSelectedResourceNames(USERS);
	}
}
