package com.marklogic.mgmt.clusters;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.ActionRequiringRestart;
import com.marklogic.mgmt.admin.AdminConfig;
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

	/**
	 * Adds a host to the cluster represented by adminManager
	 * @param adminManager AdminManager for a host already in the cluster
	 * @param hostname hostname of the joining host
	 * @param username admin username of the joining host
	 * @param password admin password of the joining host
	 * @param group name of the group within the cluster to add the joining host to
	 * @throws Exception
	 */
    public void addHost(AdminManager adminManager, String hostname, String username, String password, String group) throws Exception {
		addHost(adminManager, hostname, username, password, group, null);
	}

	/**
	 * Adds a host to the cluster represented by adminManager
	 * @param adminManager AdminManager for a host already in the cluster
	 * @param hostname hostname of the joining host
	 * @param username admin username of the joining host
	 * @param password admin password of the joining host
	 * @param group name of the group within the cluster to add the joining host to
	 * @param zone zone information for the joining host (optional)
	 * @throws Exception
	 */
    public void addHost(AdminManager adminManager, String hostname, String username, String password, String group, String zone) throws Exception{
		AdminConfig joiningHostAdminConfig = new AdminConfig(hostname, 8001, username, password);
		AdminManager joiningHostAdminManager = new AdminManager(joiningHostAdminConfig);

		// get the joining host's configuration
		Fragment fragment = joiningHostAdminManager.getServerConfig();

		// Make sure host has previously been initialized. If it hasn't, the host-id from the getServerConfig will
		// be empty
		String hostId = fragment.getElementValue("node()/m:host-id");
		if(hostId.isEmpty()){
			logger.error("New host [" + hostname + "] has not been initialized. Please initialize the host first.");
			return;
		}

		// Send the joining host's config to the bootstrap host and receive
		// the cluster config data needed to complete the join.
		byte[] clusterConfigZipBytes = adminManager.postJoiningHostConfig(fragment, group, zone);
		if(clusterConfigZipBytes == null){
			logger.error("Error sending new host's config to cluster and receiving updated cluster configuration");
			return;
		}

		// Final step of adding a host to the cluster. Post the zipped cluster config to the
		// joining host
		joiningHostAdminManager.postClustConfigToJoiningHost(clusterConfigZipBytes);

	}

	/**
	 * Removes the specified host from the cluster. Assumes connection information is of the host to remove
	 * @param hostname hostname of the server to remove from the cluster
	 */
	public void removeHost(String hostname){
		AdminConfig adminConfig = new AdminConfig(hostname, 8001, manageClient.getManageConfig().getUsername(), manageClient.getManageConfig().getPassword());
		AdminManager adminManager = new AdminManager(adminConfig);
		adminManager.leaveCluster();

	}

}
