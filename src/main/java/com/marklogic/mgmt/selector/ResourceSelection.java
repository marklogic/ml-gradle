package com.marklogic.mgmt.selector;

public interface ResourceSelection {

	String[] getDatabaseNames();

	String[] getPrivilegeExecuteNames();

	String[] getPrivilegeUriNames();

	String[] getRoleNames();

	String[] getServerNames();

	String[] getTaskNames();

	String[] getUserNames();
}
