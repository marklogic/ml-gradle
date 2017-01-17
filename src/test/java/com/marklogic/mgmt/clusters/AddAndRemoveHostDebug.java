package com.marklogic.mgmt.clusters;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.hosts.HostManager;
import org.junit.Assert;

/**
 * Basic test to test adding a new host to a cluster and then removing it.
 *
 * Note: The new host must have already been initialized
 * Note: After running this test, the new host will have been removed from the cluster and
 *       will need to be re-initialized before this test can be run again.
 */
public class AddAndRemoveHostDebug {

	public static void main(String[] args) throws Exception{
		String existingHost = args[0];
		String existingHostPassword = args[1];
		String joiningHost = args[2];
		String joiningHostPassword = args[3];

		ManageConfig manageConfig = new ManageConfig(existingHost, 8002, "admin", existingHostPassword);
		ManageClient manageClient = new ManageClient(manageConfig);

		HostManager hostManager = new HostManager(manageClient);

		Assert.assertEquals("Expecting only one host to start out with", 1, hostManager.getHostIds().size());

		// Add joining host to the cluster
		ClusterManager clusterManager = new ClusterManager(manageClient);
		AdminConfig adminConfig = new AdminConfig(existingHost, 8001, "admin", existingHostPassword);
		AdminManager adminManager = new AdminManager(adminConfig);
		clusterManager.addHost(adminManager, joiningHost, "admin", joiningHostPassword, "Default");

		Assert.assertEquals("Expecting two hosts after the add", 2, hostManager.getHostIds().size());
		System.out.println("After adding a host, the cluster now has two hosts");

		// Remove joining host from cluster
		clusterManager.removeHost(joiningHost);

		Assert.assertEquals("Expecting 1 host after removing", 1, hostManager.getHostIds().size());
		System.out.println("After removing a host, the cluster now has one host");


	}
}
