package com.marklogic.appdeployer.command.cpf;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.mgmt.cpf.PipelineManager;
import com.marklogic.rest.util.ResourcesFragment;

public class LoadDefaultCpfPipelinesTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void loadDefaultCpfPipelines() {
        initializeAppDeployer(new CreateRestApiServersCommand(), new UpdateContentDatabasesCommand(),
                new CreateTriggersDatabaseCommand());

        appDeployer.deploy(appConfig);

        String dbName = appConfig.getTriggersDatabaseName();

        PipelineManager mgr = new PipelineManager(manageClient);
        mgr.loadDefaultPipelines(dbName);

        ResourcesFragment f = mgr.getAsXml(dbName);
        assertEquals("As of ML 8.0-3, 23 default pipelines should have been loaded", 23, f.getResourceCount());
    }
}
