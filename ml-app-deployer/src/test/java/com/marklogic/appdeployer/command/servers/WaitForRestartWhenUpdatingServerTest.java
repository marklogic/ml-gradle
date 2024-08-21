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
package com.marklogic.appdeployer.command.servers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class WaitForRestartWhenUpdatingServerTest extends AbstractAppDeployerTest {

	@AfterEach
	public void tearDown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/single-server")));
		initializeAppDeployer(new DeployOtherServersCommand());
		appConfig.getCustomTokens().put("%%HTTP_PORT%%", "8048");
		appDeployer.deploy(appConfig);

		// Now change the port, and then redeploy, and immediately deploy again to verify that the redeploy waits for
		// ML to restart
		appConfig.getCustomTokens().put("%%HTTP_PORT%%", "8049");
		appDeployer.deploy(appConfig);
		appDeployer.deploy(appConfig);

		// Nothing to verify - the lack of an error means that the command waited for ML to restart
	}
}
