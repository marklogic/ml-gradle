package com.rjrudin.marklogic.appdeployer.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.command.databases.CreateContentDatabasesCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand;
import com.rjrudin.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;

public class SimpleAppDeployerTest extends Assert {

    private SimpleAppDeployer deployer;
    private CreateRestApiServersCommand restApiCommand;
    private CreateContentDatabasesCommand dbCommand;

    @Before
    public void setup() {
        restApiCommand = new CreateRestApiServersCommand();
        dbCommand = new CreateContentDatabasesCommand();
        deployer = new SimpleAppDeployer(restApiCommand, dbCommand);
    }

    @Test
    public void getCommandOfType() {
        assertEquals(restApiCommand, deployer.getCommandOfType(CreateRestApiServersCommand.class));
        assertEquals(dbCommand, deployer.getCommandOfType(CreateContentDatabasesCommand.class));
        assertNull(deployer.getCommandOfType(CreateTriggersDatabaseCommand.class));
    }

    @Test
    public void getCommand() {
        assertEquals(restApiCommand, deployer.getCommand("CreateRestApiServersCommand"));
        assertEquals(dbCommand, deployer.getCommand("CreateContentDatabasesCommand"));
        assertNull(deployer.getCommand("SomeOtherCommand"));
    }
}
