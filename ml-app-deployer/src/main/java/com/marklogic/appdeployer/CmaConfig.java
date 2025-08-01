/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer;

public class CmaConfig {

	private boolean combineRequests;
	private boolean deployAmps;
	private boolean deployDatabases;
	private boolean deployForests;
	private boolean deployPrivileges;
	private boolean deployProtectedPaths;
	private boolean deployQueryRolesets;
	private boolean deployRoles;
	private boolean deployServers;
	private boolean deployUsers;

	public CmaConfig() {
	}

	public CmaConfig(boolean enableAll) {
		if (enableAll) {
			enableAll();
		}
	}

	public void enableAll() {
		setCombineRequests(true);
		setDeployAmps(true);
		setDeployDatabases(true);
		setDeployForests(true);
		setDeployPrivileges(true);
		setDeployProtectedPaths(true);
		setDeployQueryRolesets(true);
		setDeployRoles(true);
		setDeployServers(true);
		setDeployUsers(true);
	}

	public boolean isDeployAmps() {
		return deployAmps;
	}

	public void setDeployAmps(boolean deployAmps) {
		this.deployAmps = deployAmps;
	}

	public boolean isDeployDatabases() {
		return deployDatabases;
	}

	public void setDeployDatabases(boolean deployDatabases) {
		this.deployDatabases = deployDatabases;
	}

	public boolean isDeployForests() {
		return deployForests;
	}

	public void setDeployForests(boolean deployForests) {
		this.deployForests = deployForests;
	}

	public boolean isDeployPrivileges() {
		return deployPrivileges;
	}

	public void setDeployPrivileges(boolean deployPrivileges) {
		this.deployPrivileges = deployPrivileges;
	}

	public boolean isDeployServers() {
		return deployServers;
	}

	public void setDeployServers(boolean deployServers) {
		this.deployServers = deployServers;
	}

	public boolean isDeployRoles() {
		return deployRoles;
	}

	public void setDeployRoles(boolean deployRoles) {
		this.deployRoles = deployRoles;
	}

	public boolean isDeployUsers() {
		return deployUsers;
	}

	public void setDeployUsers(boolean deployUsers) {
		this.deployUsers = deployUsers;
	}

	public boolean isCombineRequests() {
		return combineRequests;
	}

	public void setCombineRequests(boolean combineRequests) {
		this.combineRequests = combineRequests;
	}

	public boolean isDeployProtectedPaths() {
		return deployProtectedPaths;
	}

	public void setDeployProtectedPaths(boolean deployProtectedPaths) {
		this.deployProtectedPaths = deployProtectedPaths;
	}

	public boolean isDeployQueryRolesets() {
		return deployQueryRolesets;
	}

	public void setDeployQueryRolesets(boolean deployQueryRolesets) {
		this.deployQueryRolesets = deployQueryRolesets;
	}
}
