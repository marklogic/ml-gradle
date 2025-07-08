/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapResourceSelection implements ResourceSelection {

	private Map<String, List<String>> selections = new HashMap<>();

	/**
	 * Clients use this method to select a resource of a particular type. The "value" parameter can differ in its
	 * nature based on the resource type. For example, for a database, the value can be a name, which is sufficient
	 * for uniquely identifying a database. But for an amp, the value should be the uriref for the amp resource, as
	 * a name is not sufficient for uniquely identifying an amp.
	 *
	 * @param type
	 * @param value
	 */
	public void select(String type, String value) {
		List<String> names = selections.computeIfAbsent(type, k -> new ArrayList<>());
		names.add(value);
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
	public String[] getCpfConfigNames() {
		return getSelectedResourceNames(CPF_CONFIGS);
	}

	@Override
	public String[] getDomainNames() {
		return getSelectedResourceNames(DOMAINS);
	}

	@Override
	public String[] getPipelineNames() {
		return getSelectedResourceNames(PIPELINES);
	}

	@Override
	public String[] getGroupNames() {
		return getSelectedResourceNames(GROUPS);
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
	public String[] getTriggerNames() {
		return getSelectedResourceNames(TRIGGERS);
	}

	@Override
	public String[] getUserNames() {
		return getSelectedResourceNames(USERS);
	}

	@Override
	public String[] getAmpUriRefs() {
		return getSelectedResourceNames(AMPS);
	}
}
