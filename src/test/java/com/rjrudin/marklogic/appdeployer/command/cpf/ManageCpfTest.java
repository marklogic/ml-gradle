package com.rjrudin.marklogic.appdeployer.command.cpf;

import org.junit.After;
import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.cpf.CreateCpfConfigsCommand;
import com.rjrudin.marklogic.appdeployer.command.cpf.CreateDomainsCommand;
import com.rjrudin.marklogic.appdeployer.command.cpf.CreatePipelinesCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.CreateSchemasDatabaseCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand;
import com.rjrudin.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.rjrudin.marklogic.mgmt.cpf.CpfConfigManager;
import com.rjrudin.marklogic.mgmt.cpf.DomainManager;
import com.rjrudin.marklogic.mgmt.cpf.PipelineManager;

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
