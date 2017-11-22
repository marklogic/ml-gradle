package com.marklogic.mgmt.resource.restapis;

/**
 * Encapsulates a call for deleting a REST API server. Defaults to deleting the modules database but not the
 * content database. For any database that should be deleted, defaults to deleting any replica forests that exist for
 * that database as well, as the call to DELETE /v1/rest-apis will not delete replica forests itself.
 */
public class RestApiDeletionRequest {

	private String serverName;
	private String groupName;

	private boolean includeContent = false;
	private boolean includeModules = true;

	private boolean deleteContentReplicaForests = true;
	private boolean deleteModulesReplicaForests = true;

	public RestApiDeletionRequest(String serverName, String groupName) {
		this.serverName = serverName;
		this.groupName = groupName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getGroupName() {
		return groupName;
	}

	public boolean isIncludeContent() {
		return includeContent;
	}

	public void setIncludeContent(boolean includeContent) {
		this.includeContent = includeContent;
	}

	public boolean isIncludeModules() {
		return includeModules;
	}

	public void setIncludeModules(boolean includeModules) {
		this.includeModules = includeModules;
	}

	public boolean isDeleteContentReplicaForests() {
		return deleteContentReplicaForests;
	}

	public void setDeleteContentReplicaForests(boolean deleteContentReplicaForests) {
		this.deleteContentReplicaForests = deleteContentReplicaForests;
	}

	public boolean isDeleteModulesReplicaForests() {
		return deleteModulesReplicaForests;
	}

	public void setDeleteModulesReplicaForests(boolean deleteModulesReplicaForests) {
		this.deleteModulesReplicaForests = deleteModulesReplicaForests;
	}
}
