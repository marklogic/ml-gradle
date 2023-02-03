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
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployServersWithCmaTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		initializeAppDeployer(new DeployOtherServersCommand());
		undeploySampleApp();
	}

	@Test
	public void withCma() {
		appConfig.getCmaConfig().setDeployServers(true);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-servers")));

		ServerManager mgr = new ServerManager(manageClient);

		initializeAppDeployer(new TestDeployOtherServersCommand());

		deploySampleApp();
		assertTrue(mgr.exists("sample-app-odbc"));
		assertTrue(mgr.exists("sample-app-xdbc"));

		deploySampleApp();
		assertTrue(mgr.exists("sample-app-odbc"));
		assertTrue(mgr.exists("sample-app-xdbc"));
	}

}

class TestDeployOtherServersCommand extends DeployOtherServersCommand {

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// This should force an error if CMA isn't used
		return null;
	}
}
