package com.marklogic.rest.mgmt.security.users;

import org.junit.Before;
import org.junit.Test;

import com.marklogic.rest.mgmt.AbstractMgmtTest;

public class ManageUsersTest extends AbstractMgmtTest {

    UserManager userMgr;

    @Before
    public void before() {
        userMgr = new UserManager(manageClient);
    }

    @Test
    public void doesUserExistTest() {
        assertTrue(userMgr.exists("admin"));
    }

    @Test
    public void doesUserNotExistTest() {
        assertFalse(userMgr.exists("admin123"));
    }

    @Test
    public void createUserTest() {
        userMgr.save("{\"user-name\":\"joe\", \"password\": \"cool\"}");
        assertTrue(userMgr.exists("joe"));
    }
}
