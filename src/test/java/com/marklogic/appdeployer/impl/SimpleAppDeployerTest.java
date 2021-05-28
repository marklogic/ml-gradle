package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleAppDeployerTest  {

    private SimpleAppDeployer deployer;
    private DeployRestApiServersCommand restApiCommand;
    private DeployOtherDatabasesCommand dbCommand;

    @BeforeEach
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
