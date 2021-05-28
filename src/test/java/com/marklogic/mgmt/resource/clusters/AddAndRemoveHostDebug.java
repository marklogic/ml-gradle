package com.marklogic.mgmt.resource.clusters;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.resource.hosts.HostManager;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test to test adding a new host to a cluster and then removing it.
 *
 * Note: The new host must have already been initialized
 * Note: After running this test, the new host will have been removed from the cluster and
 *       will need to be re-initialized before this test can be run again.
 */
public class AddAndRemoveHostDebug {

	public static void main(String[] args) throws Exception {
		String existingHost = args[0];
		String existingHostPassword = args[1];
		String joiningHost = args[2];

		ManageConfig manageConfig = new ManageConfig(existingHost, 8002, "admin", existingHostPassword);
		ManageClient manageClient = new ManageClient(manageConfig);

		HostManager hostManager = new HostManager(manageClient);

		assertEquals(1, hostManager.getHostIds().size(), "Expecting only one host to start out with");

		// Add joining host to the cluster
		ClusterManager clusterManager = new ClusterManager(manageClient);
		AdminConfig adminConfig = new AdminConfig(existingHost, 8001, "admin", existingHostPassword);
		AdminManager adminManager = new AdminManager(adminConfig);
		clusterManager.addHost(adminManager, joiningHost);

		assertEquals(2, hostManager.getHostIds().size(), "Expecting two hosts after the add");
		System.out.println("After adding a host, the cluster now has two hosts");

		// Remove joining host from cluster
		clusterManager.removeHost(joiningHost);

		assertEquals(1, hostManager.getHostIds().size(), "Expecting 1 host after removing");
		System.out.println("After removing a host, the cluster now has one host");


	}
}
