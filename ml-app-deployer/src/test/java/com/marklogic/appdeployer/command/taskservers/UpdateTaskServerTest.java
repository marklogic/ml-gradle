/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.taskservers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.taskservers.TaskServerManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateTaskServerTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		TaskServerManager mgr = new TaskServerManager(manageClient);
		final int currentThreadCount = Integer.parseInt(mgr.getPropertiesAsXml().getElementValue("/node()/m:threads"));

		initializeAppDeployer(new UpdateTaskServerCommand());
		deploySampleApp();

		try {
			Fragment xml = mgr.getPropertiesAsXml();
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:log-errors"));
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:debug-allow"));
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:profile-allow"));
		} finally {
			ObjectNode payload = ObjectMapperFactory.getObjectMapper().createObjectNode();
			payload.put("threads", currentThreadCount);
			payload.put("log-errors", true);
			payload.put("debug-allow", true);
			payload.put("profile-allow", true);
			mgr.updateTaskServer("TaskServer", payload.toString(), adminManager);

			Fragment xml = mgr.getPropertiesAsXml();
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:log-errors"));
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:debug-allow"));
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:profile-allow"));
		}
	}
}
