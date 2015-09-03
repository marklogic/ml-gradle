package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.ConfigDir;
import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.PrivilegeManager;
import com.rjrudin.marklogic.mgmt.security.RoleManager;
import com.rjrudin.marklogic.rest.util.Fragment;

public class ManageRolesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new RoleManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployRolesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-role1", "sample-app-role2" };
    }

    @Test
    public void updateRole() {
        RoleManager mgr = new RoleManager(manageClient);
        initializeAppDeployer(new DeployRolesCommand());

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

    @Test
    public void roleWithCustomPrivilege() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/role-with-privilege-config")));

        RoleManager roleMgr = new RoleManager(manageClient);
        PrivilegeManager privMgr = new PrivilegeManager(manageClient);

        initializeAppDeployer(new DeployRolesCommand(), new DeployPrivilegesCommand());
        appDeployer.deploy(appConfig);

        try {
            assertTrue(roleMgr.exists("sample-app-role1"));
            assertTrue(privMgr.exists("sample-app-execute-1"));
        } finally {
            undeploySampleApp();

            assertFalse(roleMgr.exists("sample-app-role1"));
            assertFalse(privMgr.exists("sample-app-execute-1"));
        }

    }
}
