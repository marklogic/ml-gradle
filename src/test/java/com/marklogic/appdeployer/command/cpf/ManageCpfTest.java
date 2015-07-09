package com.marklogic.appdeployer.command.cpf;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.CreateSchemasDatabaseCommand;
import com.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.mgmt.cpf.CpfConfigManager;
import com.marklogic.rest.mgmt.cpf.DomainManager;
import com.marklogic.rest.mgmt.cpf.PipelineManager;

public class ManageCpfTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void test() {
        initializeAppDeployer(new CreateRestApiServersCommand(), new UpdateContentDatabasesCommand(),
                new CreateSchemasDatabaseCommand(), new CreateTriggersDatabaseCommand(), new CreateDomainsCommand(),
                new CreateCpfConfigsCommand(), new CreatePipelinesCommand());

        appDeployer.deploy(appConfig);

        String dbName = appConfig.getTriggersDatabaseName();
        assertEquals(2, new DomainManager(manageClient).getAsXml(dbName).getResourceCount());
        assertEquals(1, new CpfConfigManager(manageClient).getAsXml(dbName).getResourceCount());
        assertEquals(1, new PipelineManager(manageClient).getAsXml(dbName).getResourceCount());
    }

}
