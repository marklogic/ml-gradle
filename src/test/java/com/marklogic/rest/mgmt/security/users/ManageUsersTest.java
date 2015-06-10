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
        assertTrue(userMgr.userExists("admin"));
    }

    @Test
    public void doesUserNotExistTest() {
        assertFalse(userMgr.userExists("admin123"));
    }
    
    @Test
    public void createUserTest() {
    	try {
    		userMgr.createUser("{\"user-name\":\"joe\", \"password\": \"cool\"}");
    		
    	} catch (Exception ex) {
    		logger.info(ex.getMessage());
    	}
    	String name = "joe";
    	assertTrue(userMgr.userExists("joe"));
    	
    }

}
