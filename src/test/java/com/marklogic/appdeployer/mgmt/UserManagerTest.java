package com.marklogic.appdeployer.mgmt;

import com.marklogic.appdeployer.mgmt.UserManager;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserManagerTest {
	
	@Test
	public void doesUserExistTest() {
		
		// Define how to connect to the ML manage API - this defaults to localhost/8002/admin/admin
    ManageConfig manageConfig = new ManageConfig();

    // Build a client for talking to the ML manage API - this wraps an instance of Spring RestTemplate with some
    // convenience methods for talking to the manage API
    ManageClient client = new ManageClient(manageConfig);
		
		UserManager userMgr = new UserManager(client.getRestTemplate(), client.getBaseUrl());
		assertTrue(userMgr.userExists("admin"));	
	}
	
	@Test
	public void doesUserNotExistTest() {
		
		// Define how to connect to the ML manage API - this defaults to localhost/8002/admin/admin
    ManageConfig manageConfig = new ManageConfig();

    // Build a client for talking to the ML manage API - this wraps an instance of Spring RestTemplate with some
    // convenience methods for talking to the manage API
    ManageClient client = new ManageClient(manageConfig);
		
		UserManager userMgr = new UserManager(client.getRestTemplate(), client.getBaseUrl());
		assertFalse(userMgr.userExists("admin123"));	
	}
	
}
