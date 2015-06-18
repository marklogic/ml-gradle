package com.marklogic.appdeployer.command.security;

import org.junit.Test;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.RoleManager;
import com.marklogic.rest.util.Fragment;

public class ManageRolesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new RoleManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new CreateRolesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-role1", "sample-app-role2" };
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
