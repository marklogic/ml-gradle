package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.template.server.ServerTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WriteServerTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployOtherServersCommand());

		buildResourceAndDeploy(new ServerTemplateBuilder());

		Server s = api.server("CHANGEME-name-of-server");
		assertEquals(new Integer(8099), s.getPort());
		assertEquals("Modules", s.getModulesDatabase());
		assertEquals("Documents", s.getContentDatabase());
		assertEquals("digest", s.getAuthentication());
		assertEquals("Default", s.getGroupName());
		assertEquals("http", s.getServerType());
		assertEquals("/", s.getRoot());
		assertTrue(s.getEnabled());
	}
}
