package com.marklogic.rest.mgmt.appservers;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class ServerManager extends AbstractResourceManager {

    private String groupName;

    public ServerManager(ManageClient manageClient, String groupName) {
        super(manageClient);
        this.groupName = groupName;
    }

    @Override
    protected String[] getDeleteResourceParams(String payload) {
        return new String[] { "group-id", groupName };
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId) {
        return format("%s/properties?group-id=%s", getResourcePath(resourceNameOrId), groupName);
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
