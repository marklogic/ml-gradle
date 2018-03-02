package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.template.group.GroupTemplateBuilder;
import org.junit.Test;

public class WriteGroupTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployGroupsCommand());

		buildResourceAndDeploy(new GroupTemplateBuilder());

		Group group = api.group("CHANGEME-name-of-group");
		assertTrue(group.getMeteringEnabled());
		assertEquals("Meters", group.getMetersDatabase());
	}
}
