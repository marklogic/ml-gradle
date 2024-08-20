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
