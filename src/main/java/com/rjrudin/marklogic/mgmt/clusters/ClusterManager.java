package com.rjrudin.marklogic.mgmt.clusters;

import com.rjrudin.marklogic.mgmt.AbstractManager;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.admin.ActionRequiringRestart;
import com.rjrudin.marklogic.mgmt.admin.AdminManager;

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
}
