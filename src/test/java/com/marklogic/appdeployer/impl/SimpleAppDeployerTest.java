package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleAppDeployerTest extends Assert {

    private SimpleAppDeployer deployer;
    private DeployRestApiServersCommand restApiCommand;
    private DeployOtherDatabasesCommand dbCommand;

    @Before
    public void setup() {
        restApiCommand = new DeployRestApiServersCommand();
        dbCommand = new DeployOtherDatabasesCommand();
        deployer = new SimpleAppDeployer(restApiCommand, dbCommand);
    }

    @Test
    public void getCommandOfType() {
        assertEquals(restApiCommand, deployer.getCommandOfType(DeployRestApiServersCommand.class));
        assertEquals(dbCommand, deployer.getCommandOfType(DeployOtherDatabasesCommand.class));
        assertNull(deployer.getCommandOfType(DeployUsersCommand.class));
    }

    @Test
    public void getCommand() {
        assertEquals(restApiCommand, deployer.getCommand("DeployRestApiServersCommand"));
        assertEquals(dbCommand, deployer.getCommand("DeployOtherDatabasesCommand"));
        assertNull(deployer.getCommand("SomeOtherCommand"));
    }

	@Test
	public void removeCommand() {
		assertEquals(restApiCommand, deployer.removeCommand("DeployRestApiServersCommand"));
		assertEquals(1, deployer.getCommands().size());
		assertEquals(dbCommand, deployer.removeCommand("DeployOtherDatabasesCommand"));
		assertTrue(deployer.getCommands().isEmpty());
	}
}
