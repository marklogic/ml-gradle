package com.marklogic.appdeployer.command.clusters;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.clusters.ClusterManager;
import org.junit.Test;

import java.io.File;

public class ModifyLocalClusterTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		final String sslFipsXpath = "/node()/node()[local-name(.) = 'ssl-fips-enabled']";

		ClusterManager mgr = new ClusterManager(manageClient);

		String originalSslFipsEnabled = mgr.getLocalClusterProperties().getElementValue(sslFipsXpath);

		/**
		 * Our test file sets "false" by default, so if the cluster is already false, just test this via the
		 * Manager class.
		 */
		if ("false".equals(originalSslFipsEnabled)) {
			mgr.modifyLocalCluster("{\"ssl-fips-enabled\":true}", adminManager);
			assertEquals("true", mgr.getLocalClusterProperties().getElementValue(sslFipsXpath));
			mgr.modifyLocalCluster("{\"ssl-fips-enabled\":false}", adminManager);
			assertEquals("false", mgr.getLocalClusterProperties().getElementValue(sslFipsXpath));
		} else {
			appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/cluster-test"));
			initializeAppDeployer(new ModifyLocalClusterCommand());
			appDeployer.deploy(appConfig);
			assertEquals("false", mgr.getLocalClusterProperties().getElementValue(sslFipsXpath));

			mgr.modifyLocalCluster("{\"ssl-fips-enabled\":true}", adminManager);
			assertEquals("true", mgr.getLocalClusterProperties().getElementValue(sslFipsXpath));
		}
	}
}
