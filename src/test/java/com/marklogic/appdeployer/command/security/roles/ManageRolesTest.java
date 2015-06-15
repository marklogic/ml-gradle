package com.marklogic.appdeployer.command.security.roles;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.rest.mgmt.security.roles.RoleManager;

public class ManageRolesTest extends AbstractAppDeployerTest {

    @Test
    public void createRolesAsPartOfDeploy() {
        RoleManager mgr = new RoleManager(manageClient);
        initializeAppDeployer(new CreateRolesCommand());

        appDeployer.deploy(appConfig);

        assertTrue(mgr.exists("sample-app-role1"));
        assertTrue(mgr.exists("sample-app-role2"));

        // Make sure we don't get an error from trying to create the roles again
        appDeployer.deploy(appConfig);

        // Now undo
        appDeployer.undeploy(appConfig);

        assertFalse(mgr.exists("sample-app-role1"));
        assertFalse(mgr.exists("sample-app-role2"));
    }
}
