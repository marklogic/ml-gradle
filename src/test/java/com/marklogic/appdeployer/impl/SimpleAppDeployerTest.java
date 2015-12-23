package com.marklogic.appdeployer.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;

public class SimpleAppDeployerTest extends Assert {

    private SimpleAppDeployer deployer;
    private DeployRestApiServersCommand restApiCommand;
    private DeployContentDatabasesCommand dbCommand;

    @Before
    public void setup() {
        restApiCommand = new DeployRestApiServersCommand();
        dbCommand = new DeployContentDatabasesCommand();
        deployer = new SimpleAppDeployer(restApiCommand, dbCommand);
    }

    @Test
    public void getCommandOfType() {
        assertEquals(restApiCommand, deployer.getCommandOfType(DeployRestApiServersCommand.class));
        assertEquals(dbCommand, deployer.getCommandOfType(DeployContentDatabasesCommand.class));
        assertNull(deployer.getCommandOfType(DeployTriggersDatabaseCommand.class));
    }

    @Test
    public void getCommand() {
        assertEquals(restApiCommand, deployer.getCommand("DeployRestApiServersCommand"));
        assertEquals(dbCommand, deployer.getCommand("DeployContentDatabasesCommand"));
        assertNull(deployer.getCommand("SomeOtherCommand"));
    }
}
