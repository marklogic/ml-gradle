package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.cpf.PipelineManager;
import com.marklogic.rest.util.ResourcesFragment;

public class LoadDefaultCpfPipelinesTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void loadDefaultCpfPipelines() {
        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployContentDatabasesCommand(),
                new DeployOtherDatabasesCommand());

        appDeployer.deploy(appConfig);

        String dbName = appConfig.getTriggersDatabaseName();

        PipelineManager mgr = new PipelineManager(manageClient, dbName);
        mgr.loadDefaultPipelines();

        ResourcesFragment f = mgr.getAsXml();
        assertEquals("As of ML 8.0-3, 23 default pipelines should have been loaded", 23, f.getResourceCount());
    }
}
