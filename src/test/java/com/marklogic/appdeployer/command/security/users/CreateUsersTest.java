package com.marklogic.appdeployer.command.security.users;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.rest.mgmt.security.users.UserManager;

public class CreateUsersTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        UserManager mgr = new UserManager(manageClient);
        initializeAppDeployer(new CreateUsersCommand());

        appDeployer.deploy(appConfig);

        assertTrue(mgr.exists("sample-app-jane"));
        assertTrue(mgr.exists("sample-app-john"));

        // Make sure we don't get an error from trying to create the users again
        appDeployer.deploy(appConfig);

        // Now undo
        appDeployer.undeploy(appConfig);

        assertFalse(mgr.exists("sample-app-jane"));
        assertFalse(mgr.exists("sample-app-john"));
    }
}
