package com.marklogic.mgmt.resource.appservers;

import com.marklogic.mgmt.SaveReceipt;
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
	 * This is hacky, but it should be close to 100% reliable. Worst case is that the payload has the string
	 * "external-security" in some other field and we unnecessarily use the security user.
	 *
	 * Public so that it can be unit-tested easily.
	 *
	 * @param payload
	 * @return
	 */
	@Override
	public boolean useSecurityUser(String payload) {
		boolean b = payload != null && payload.contains("external-security");
		if (b && logger.isInfoEnabled()) {
			logger.info("Server payload contains external-security, so using the security user");
		}
		return b;
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
		if (logger.isInfoEnabled()) {
			logger.info("Checking for existence of resource: " + resourceNameOrId);
		}
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
