package com.marklogic.appdeployer.command.cpf;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.cpf.PipelineManager;
import com.marklogic.rest.util.ResourcesFragment;

public class LoadDefaultCpfPipelinesTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void loadDefaultCpfPipelines() {
        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployContentDatabasesCommand(),
                new DeploySchemasDatabaseCommand(), new DeployTriggersDatabaseCommand());

        appDeployer.deploy(appConfig);

        String dbName = appConfig.getTriggersDatabaseName();

        PipelineManager mgr = new PipelineManager(manageClient);
        mgr.loadDefaultPipelines(dbName);

        ResourcesFragment f = mgr.getAsXml(dbName);
        assertEquals("As of ML 8.0-3, 23 default pipelines should have been loaded", 23, f.getResourceCount());
    }
}
