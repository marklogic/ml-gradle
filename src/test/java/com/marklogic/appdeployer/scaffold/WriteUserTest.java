package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.template.security.UserTemplateBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WriteUserTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployUsersCommand());

		buildResourceAndDeploy(new UserTemplateBuilder());

		User user = api.user("CHANGEME-name-of-user");
		assertEquals("CHANGEME description of user", user.getDescription());
		assertNull(user.getPassword(), "A default password is created, but the Manage API of course won't return it");

		List<String> roles = user.getRole();
		assertEquals(2, roles.size());
		assertTrue(roles.contains("rest-reader"));
		assertTrue(roles.contains("rest-writer"));
	}
}
