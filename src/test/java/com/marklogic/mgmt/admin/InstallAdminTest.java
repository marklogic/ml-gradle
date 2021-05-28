package com.marklogic.mgmt.admin;

import org.junit.jupiter.api.Test;

import com.marklogic.mgmt.AbstractMgmtTest;

public class InstallAdminTest extends AbstractMgmtTest {

    /**
     * Since this test suite assumes that MarkLogic has already been properly initialized, including having an admin
     * user installed, this is just a smoke test to ensure that we don't get an error when trying to install the admin
     * again. Instead, a message should be logged and ML should not be restarted.
     */
    @Test
    public void adminAlreadyInstalled() {
        adminManager.installAdmin("admin", "admin");
    }
}
