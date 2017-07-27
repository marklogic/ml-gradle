package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.api.security.Amp;

public interface ResourceSelection {

	String AMPS = "amps";
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

	/**
	 * Because an amp cannot be uniquely identified solely by its name, the implementation is expected to return the
	 * full "uriref" that the Manage API defines for an amp, as a uriref uniquely identifies the amp.
	 *
	 * @return
	 */
	String[] getAmpUriRefs();
}
