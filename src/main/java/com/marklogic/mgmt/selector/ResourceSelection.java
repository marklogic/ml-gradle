package com.marklogic.mgmt.selector;

public interface ResourceSelection {

	String DATABASES = "databases";
	String PRIVILEGES_EXECUTE = "privilegesExecute";
	String PRIVILEGES_URI = "privilegesUri";
	String ROLES = "roles";
	String SERVERS = "servers";
	String TASKS = "tasks";
	String USERS = "users";

	String[] getDatabaseNames();

	String[] getPrivilegeExecuteNames();

	String[] getPrivilegeUriNames();

	String[] getRoleNames();

	String[] getServerNames();

	String[] getTaskNames();

	String[] getUserNames();
}
