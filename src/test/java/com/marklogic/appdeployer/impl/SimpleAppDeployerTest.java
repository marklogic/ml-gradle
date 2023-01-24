package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import static org.junit.jupiter.api.Assertions.*;

import com.marklogic.mgmt.AbstractMgmtTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SimpleAppDeployerTest extends AbstractMgmtTest {

    private SimpleAppDeployer deployer;
    private DeployRestApiServersCommand restApiCommand;
    private DeployOtherDatabasesCommand dbCommand;

    @BeforeEach
    public void setup() {
        restApiCommand = new DeployRestApiServersCommand();
        dbCommand = new DeployOtherDatabasesCommand();
        deployer = new SimpleAppDeployer(manageClient, null, restApiCommand, dbCommand);
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

	@Test
	void constructWithList() {
    	deployer = new SimpleAppDeployer(Arrays.asList(new DeployOtherDatabasesCommand()));
    	assertEquals(1, deployer.getCommands().size());
	}

	@Test
	void constructWithArray() {
    	deployer = new SimpleAppDeployer(manageClient, null, new DeployOtherDatabasesCommand());
    	assertEquals(1, deployer.getCommands().size());
	}
}
