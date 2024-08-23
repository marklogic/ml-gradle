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
package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand;
import com.marklogic.mgmt.api.API;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateRestApiWithCustomRewriterTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	/**
	 * This was created for #242, but is being modified for #256 as this library is now using ServerManager instead
	 * of /v1/rest-apis to see if a REST server exists already.
	 */
	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/rest-api-different-rewriter")));

		initializeAppDeployer(new DeployRestApiServersCommand(true), new UpdateRestApiServersCommand());

		deploySampleApp();
		assertEquals("/my/custom/rewriter.xml", new API(manageClient).server("sample-app").getUrlRewriter());

		// This shouldn't throw an error, because it's no longer using /v1/rest-apis to see if the REST server exists
		deploySampleApp();
		assertEquals("/my/custom/rewriter.xml", new API(manageClient).server("sample-app").getUrlRewriter());
	}
}
