package com.marklogic.appdeployer.command.cpf;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;

public class ManageCpfTest extends AbstractAppDeployerTest {

    @Test
    public void test() throws Exception {
        initializeAppDeployer(new CreateRestApiServersCommand(), new UpdateContentDatabasesCommand(),
                new CreateTriggersDatabaseCommand(), new CreateDomainsCommand());

        appDeployer.deploy(appConfig);
    }

}
