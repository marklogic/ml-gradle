package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class ExportServerTest extends AbstractExportTest {

	@After
	public void teardown() {
		//undeploySampleApp();
	}

	@Test
	public void test() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/default-modules-database-config"));
		initializeAppDeployer(new DeployRestApiServersCommand(true));
		deploySampleApp();

		new Exporter(manageClient).servers("sample-app").export(exportDir);

		undeploySampleApp();

		appConfig.getConfigDir().setBaseDir(exportDir);
		initializeAppDeployer(new DeployOtherServersCommand(), new DeployOtherDatabasesCommand());
		deploySampleApp();
	}
}
