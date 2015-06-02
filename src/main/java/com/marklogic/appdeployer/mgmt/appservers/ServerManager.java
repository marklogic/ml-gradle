package com.marklogic.appdeployer.mgmt.appservers;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.mgmt.ManageClient;

public class ServerManager extends AbstractManager {

    private ManageClient manageClient;

    public ServerManager(ManageClient manageClient) {
        this.manageClient = manageClient;
    }

    public void updateServer(String serverIdOrName, String groupIdOrName, String json) {
        String path = format("/manage/v2/servers/%s/properties?group-id=%s", serverIdOrName, groupIdOrName);
        // TODO Log the JSON at debug level?
        logger.info(format("Updating server %s", serverIdOrName));
        manageClient.putJson(path, json);
        logger.info(format("Updated server %s", serverIdOrName));
    }

    /**
     * Useful method for when you need to delete multiple REST API servers that point at the same modules database - set
     * the modules database to Documents for all but one, and then you can safely delete all of them.
     * 
     * @param serverIdOrName
     */
    public void setModulesDatabaseToDocuments(String serverIdOrName, String groupIdOrName) {
        String json = "{\"modules-database\":\"Documents\"}";
        updateServer(serverIdOrName, groupIdOrName, json);
    }

}
