package com.marklogic.appdeployer.command.security;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.security.CreateRolesCommand;
import com.marklogic.rest.mgmt.security.RoleManager;
import com.marklogic.rest.util.Fragment;

public class ManageRolesTest extends AbstractAppDeployerTest {

    @Test
    public void createRolesAsPartOfDeploy() {
        RoleManager mgr = new RoleManager(manageClient);
        initializeAppDeployer(new CreateRolesCommand());

        appDeployer.deploy(appConfig);

        assertTrue(mgr.exists("sample-app-role1"));
        assertTrue(mgr.exists("sample-app-role2"));

        try {
            // Make sure we don't get an error from trying to create the roles again
            appDeployer.deploy(appConfig);
        } finally {
            // Now undo
            appDeployer.undeploy(appConfig);

            assertFalse(mgr.exists("sample-app-role1"));
            assertFalse(mgr.exists("sample-app-role2"));
        }
    }

    @Test
    public void updateRole() {
        RoleManager mgr = new RoleManager(manageClient);
        initializeAppDeployer(new CreateRolesCommand());

        appDeployer.deploy(appConfig);

        assertTrue(mgr.exists("sample-app-role1"));

        try {
            mgr.save("{\"role-name\": \"sample-app-role1\", \"description\":\"This is an updated description\"}");

            Fragment f = mgr.getAsXml("sample-app-role1");
            assertTrue("The save call should either create or update a role",
                    f.elementExists("/msec:role-default/msec:description[. = 'This is an updated description']"));
        } finally {
            appDeployer.undeploy(appConfig);
        }

    }
}
