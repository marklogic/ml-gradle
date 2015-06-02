package com.marklogic.rest.mgmt.security.users;

import org.junit.Test;

import com.marklogic.rest.mgmt.AbstractMgmtTest;

public class ManageUsersTest extends AbstractMgmtTest {

    @Test
    public void doesUserExistTest() {
        UserManager userMgr = new UserManager(manageClient);
        assertTrue(userMgr.userExists("admin"));
    }

    @Test
    public void doesUserNotExistTest() {
        UserManager userMgr = new UserManager(manageClient);
        assertFalse(userMgr.userExists("admin123"));
    }

}
