package com.marklogic.mgmt.selector;

public interface ResourceSelection {

	String AMPS = "amps";
	String CPF_CONFIGS = "cpfConfigs";
	String DATABASES = "databases";
	String DOMAINS = "domains";
	String GROUPS = "groups";
	String PIPELINES = "pipelines";
	String PRIVILEGES_EXECUTE = "privilegesExecute";
	String PRIVILEGES_URI = "privilegesUri";
	String ROLES = "roles";
	String SERVERS = "servers";
	String TASKS = "tasks";
	String TRIGGERS = "triggers";
	String USERS = "users";

	String[] getCpfConfigNames();

	String[] getDatabaseNames();

	String[] getDomainNames();

	String[] getGroupNames();

	String[] getPipelineNames();

	String[] getPrivilegeExecuteNames();

	String[] getPrivilegeUriNames();

	String[] getRoleNames();

	String[] getServerNames();

	String[] getTaskNames();

	String[] getTriggerNames();

	String[] getUserNames();

	/**
	 * Because an amp cannot be uniquely identified solely by its name, the implementation is expected to return the
	 * full "uriref" that the Manage API defines for an amp, as a uriref uniquely identifies the amp.
	 *
	 * @return
	 */
	String[] getAmpUriRefs();
}
