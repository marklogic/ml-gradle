package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.template.security.PrivilegeTemplateBuilder;
import org.junit.Test;

import java.util.List;

public class WritePrivilegeTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployPrivilegesCommand());
		buildResourceAndDeploy(new PrivilegeTemplateBuilder());

		Privilege p = api.privilegeExecute("CHANGEME-name-of-privilege");
		assertEquals("CHANGEME", p.getAction());
		assertEquals("execute", p.getKind());

		List<String> roles = p.getRole();
		assertEquals(2, roles.size());
		assertTrue(roles.contains("rest-reader"));
		assertTrue(roles.contains("rest-writer"));
	}

	@Test
	public void uriPrivilege() {
		initializeAppDeployer(new DeployPrivilegesCommand());

		propertyMap.put("privilege-name", "CHANGEME-uri-privilege");
		propertyMap.put("action", "test");
		propertyMap.put("kind", "uri");
		buildResourceAndDeploy(new PrivilegeTemplateBuilder());

		Privilege p = api.privilegeUri("CHANGEME-uri-privilege");
		// Odd, the Manage API automatically appends a "/"
		assertEquals("test/", p.getAction());
		assertEquals("uri", p.getKind());

		List<String> roles = p.getRole();
		assertEquals(2, roles.size());
		assertTrue(roles.contains("rest-reader"));
		assertTrue(roles.contains("rest-writer"));
	}
}
