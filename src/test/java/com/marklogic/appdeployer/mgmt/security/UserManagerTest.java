package com.marklogic.appdeployer.mgmt.security;

import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.ManageConfig;
import com.marklogic.rest.mgmt.security.UserManager;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserManagerTest {
	
	private ManageClient client;
	
	@Before
	public void setupServiceManager() {
		// Define how to connect to the ML manage API - this defaults to localhost/8002/admin/admin
    ManageConfig manageConfig = new ManageConfig();

    // Build a client for talking to the ML manage API - this wraps an instance of Spring RestTemplate with some
    // convenience methods for talking to the manage API
    client = new ManageClient(manageConfig);
	}
	
	
	@Test
	public void doesUserExistTest() {
		UserManager userMgr = new UserManager(client);
		assertTrue(userMgr.userExists("admin"));	
	}
	
	@Test
	public void doesUserNotExistTest() {		
		UserManager userMgr = new UserManager(client);
		assertFalse(userMgr.userExists("admin123"));	
	}
	
}
