package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.cpf.CpfConfigManager;
import com.marklogic.mgmt.resource.cpf.DomainManager;
import com.marklogic.mgmt.resource.cpf.PipelineManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManageCpfTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		initializeAppDeployer(new DeployRestApiServersCommand(),
			new DeployOtherDatabasesCommand(1), new DeployDomainsCommand(),
			new DeployCpfConfigsCommand(), new DeployPipelinesCommand());

		appDeployer.deploy(appConfig);

		String dbName = appConfig.getTriggersDatabaseName();
		assertEquals(2, new DomainManager(manageClient, dbName).getAsXml().getResourceCount());
		assertEquals(1, new CpfConfigManager(manageClient, dbName).getAsXml().getResourceCount());
		assertEquals(1, new PipelineManager(manageClient, dbName).getAsXml().getResourceCount());
	}

}
