/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.CredentialsManager;
import com.marklogic.rest.util.Fragment;

import static org.junit.jupiter.api.Assertions.*;

public class DeployCredentialsTest extends AbstractManageResourceTest {

	@Override
	protected ResourceManager newResourceManager() {
		return new CredentialsManager(manageClient);
	}

	@Override
	protected Command newCommand() {
		return new DeployCredentialsCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[]{};
	}

	@Override
	protected void afterResourcesCreated() {
		Fragment f =  manageClient.getXml(new CredentialsManager(manageClient).getResourcesPath()+"?format=xml");
		assertEquals("AWS-ACCESS-KEY", f.getElementValue("/creds:credentials-properties/creds:aws/creds:access-key"));
		assertEquals("AZURE-STORAGE-ACCOUNT", f.getElementValue("/creds:credentials-properties/creds:azure/creds:storage-account"));
	}

	@Override
	protected void verifyResourcesWereDeleted(ResourceManager mgr) {
		Fragment f =  manageClient.getXml(new CredentialsManager(manageClient).getResourcesPath()+"?format=xml");
        assertNull(f.getElementValue("/creds:credentials-properties/creds:aws/creds:access-key"));
        assertNull(f.getElementValue("/creds:credentials-properties/creds:azure/creds:storage-account"));
	}
}
