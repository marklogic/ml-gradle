package com.marklogic.appdeployer.command.alert;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.alert.AlertConfigManager;

public class ManageAlertConfigsTest extends AbstractManageResourceTest {

	@Override
	protected void initializeAndDeploy() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/alert-config"));
		initializeAppDeployer(new DeployTriggersDatabaseCommand(), new DeployContentDatabasesCommand(1),
			new DeployOtherDatabasesCommand(), newCommand());
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
