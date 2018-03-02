package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployAmpsCommand;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.template.security.AmpTemplateBuilder;
import org.junit.Test;

import java.util.List;

public class WriteAmpTest extends AbstractResourceWriterTest {

	@Test
	public void test() {
		initializeAppDeployer(new DeployAmpsCommand());
		buildResourceAndDeploy(new AmpTemplateBuilder());

		Amp amp = api.amp(
			"CHANGEME-name-of-the-function-to-amp",
			"CHANGEME-namespace-of-the-module",
			"CHANGEME-module-path", "Modules");
		List<String> roles = amp.getRole();
		assertEquals(2, roles.size());
		assertTrue(roles.contains("rest-reader"));
		assertTrue(roles.contains("rest-writer"));
	}
}
