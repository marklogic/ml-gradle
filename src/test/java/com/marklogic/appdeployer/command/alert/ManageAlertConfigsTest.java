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
package com.marklogic.appdeployer.command.alert;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.alert.AlertConfigManager;

import java.io.File;

/**
 * This test is also expected to verify the fix for #442, which is that when a database resource directory - in this
 * case, sample-app/alert-configs/databases/unknown-database - cannot be associated with an existing ML database, an
 * exception is NOT thrown but rather a warning is logged. The fact that the test succeeds is evidence of this.
 */
public class ManageAlertConfigsTest extends AbstractManageResourceTest {

	@Override
	protected void initializeAndDeploy() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/alert-config"));
		initializeAppDeployer(new DeployOtherDatabasesCommand(1), newCommand());
		appDeployer.deploy(appConfig);
	}

	@Override
	protected ResourceManager newResourceManager() {
		return new AlertConfigManager(manageClient, appConfig.getContentDatabaseName());
	}

	@Override
	protected Command newCommand() {
		return new DeployAlertConfigsCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[]{"my-alert-config"};
	}

	/**
	 * No need to do anything here, as "undo" isn't yet supported for alert configs (for now, they're deleted when the
	 * content database is deleted).
	 */
	@Override
	protected void verifyResourcesWereDeleted(ResourceManager mgr) {
	}

}
