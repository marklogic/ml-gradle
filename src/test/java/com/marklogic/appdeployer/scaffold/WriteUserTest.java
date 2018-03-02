package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.template.security.UserTemplateBuilder;
import org.junit.Test;

import java.util.List;

public class WriteUserTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployUsersCommand());

		buildResourceAndDeploy(new UserTemplateBuilder());

		User user = api.user("CHANGEME-name-of-user");
		assertEquals("CHANGEME description of user", user.getDescription());
		assertNull("A default password is created, but the Manage API of course won't return it", user.getPassword());

		List<String> roles = user.getRole();
		assertEquals(2, roles.size());
		assertTrue(roles.contains("rest-reader"));
		assertTrue(roles.contains("rest-writer"));
	}
}
