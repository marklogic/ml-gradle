package com.marklogic.mgmt.resource.appservers;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;

public class ServerManager extends AbstractResourceManager {

    public final static String DEFAULT_GROUP = "Default";

    private String groupName;

    public ServerManager(ManageClient manageClient) {
        this(manageClient, DEFAULT_GROUP);
    }

    public ServerManager(ManageClient manageClient, String groupName) {
        super(manageClient);
        this.groupName = groupName != null ? groupName : DEFAULT_GROUP;
    }

	/**
	 * When doing an existence check, have to take the group name into account.
	 *
	 * @param resourceNameOrId
	 * @param resourceUrlParams
	 * @return
	 */
	@Override
	public boolean exists(String resourceNameOrId, String... resourceUrlParams) {
		String path = getResourcesPath();
		if (groupName != null) {
			path += "?group-id=" + groupName;
		}
		Fragment f = useSecurityUser() ? getManageClient().getXmlAsSecurityUser(path)
			: getManageClient().getXml(path);
		return new ResourcesFragment(f).resourceExists(resourceNameOrId);
	}

	@Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return format("%s/%s?group-id=%s", getResourcesPath(), resourceNameOrId, groupName);
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return format("%s/%s/properties?group-id=%s", getResourcesPath(), resourceNameOrId, groupName);
    }

    /**
     * Useful method for when you need to delete multiple REST API servers that point at the same modules database - set
     * the modules database to Documents for all but one, and then you can safely delete all of them.
     */
    public void setModulesDatabaseToDocuments(String serverName) {
        String payload = format("{\"server-name\":\"%s\", \"group-name\": \"%s\", \"modules-database\":\"Documents\"}",
                serverName, groupName);
        save(payload);
    }
}
