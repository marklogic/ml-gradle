package com.marklogic.appdeployer;

public class CmaConfig {

	private boolean deployAmps = false;
	private boolean deployDatabases = false;
	private boolean deployForests = false;
	private boolean deployPrivileges = false;
	private boolean deployServers = false;

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
}
