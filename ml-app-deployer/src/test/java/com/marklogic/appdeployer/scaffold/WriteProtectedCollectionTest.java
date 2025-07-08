/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand;
import com.marklogic.mgmt.api.security.Permission;
import com.marklogic.mgmt.api.security.ProtectedCollection;
import com.marklogic.mgmt.template.security.ProtectedCollectionTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WriteProtectedCollectionTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployProtectedCollectionsCommand());
		buildResourceAndDeploy(new ProtectedCollectionTemplateBuilder());

		ProtectedCollection pc = api.protectedCollection("CHANGEME-collection-to-protect");
		assertNotNull(pc);

		if (false) {
			// Not running this due to https://bugtrack.marklogic.com/55358, which prevents permissions from
			// being returned from the Manage API on protected collections
			assertEquals(1, pc.getPermission().size());
			Permission perm = pc.getPermission().get(0);
			assertEquals("rest-reader", perm.getRoleName());
			assertEquals("update", perm.getCapability());
		}
	}
}
