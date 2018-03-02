package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand;
import com.marklogic.mgmt.api.security.Permission;
import com.marklogic.mgmt.api.security.ProtectedCollection;
import com.marklogic.mgmt.template.security.ProtectedCollectionTemplateBuilder;
import org.junit.Test;

public class WriteProtectedCollectionTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployProtectedCollectionsCommand());
		buildResourceAndDeploy(new ProtectedCollectionTemplateBuilder());

		ProtectedCollection pc = api.protectedCollection("CHANGEME-collection-to-protect");
		assertEquals(1, pc.getPermission().size());
		Permission perm = pc.getPermission().get(0);
		assertEquals("rest-reader", perm.getRoleName());
		assertEquals("update", perm.getCapability());
	}
}
