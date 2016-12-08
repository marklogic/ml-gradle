package com.marklogic.mgmt.clusters;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.ActionRequiringRestart;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.rest.util.Fragment;

public class ClusterManager extends AbstractManager {

    private ManageClient manageClient;

    public ClusterManager(ManageClient client) {
        this.manageClient = client;
    }

    /**
     * This does not wait for the cluster to be available again; to do so, you'd need to use AdminManager.
     */
    public void restartLocalCluster() {
        manageClient.postJson("/manage/v2", "{\"operation\":\"restart-local-cluster\"}");
    }

    public void restartLocalCluster(AdminManager adminManager) {
        adminManager.invokeActionRequiringRestart(new ActionRequiringRestart() {
            @Override
            public boolean execute() {
                restartLocalCluster();
                return true;
            }
        });
    }

    public String getVersion() {
    	Fragment f = manageClient.getXml("/manage/v2");
	    return f.getElementValue("/c:local-cluster-default/c:version");
    }
}
