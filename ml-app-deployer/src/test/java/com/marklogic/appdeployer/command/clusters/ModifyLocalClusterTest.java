/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.clusters;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.clusters.ClusterManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
			appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/cluster-test"));
			initializeAppDeployer(new ModifyLocalClusterCommand());
			appDeployer.deploy(appConfig);
			assertEquals("false", mgr.getLocalClusterProperties().getElementValue(sslFipsXpath));

			mgr.modifyLocalCluster("{\"ssl-fips-enabled\":true}", adminManager);
			assertEquals("true", mgr.getLocalClusterProperties().getElementValue(sslFipsXpath));
		}
	}
}
