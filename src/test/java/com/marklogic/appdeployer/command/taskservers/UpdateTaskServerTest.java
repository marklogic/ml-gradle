package com.marklogic.appdeployer.command.taskservers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.taskservers.TaskServerManager;
import com.marklogic.rest.util.Fragment;
import org.junit.Test;

public class UpdateTaskServerTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		TaskServerManager mgr = new TaskServerManager(manageClient);

		initializeAppDeployer(new UpdateTaskServerCommand());
		deploySampleApp();

		try {
			Fragment xml = mgr.getPropertiesAsXml();
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:log-errors"));
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:debug-allow"));
			assertEquals("false", xml.getElementValue("/m:task-server-properties/m:profile-allow"));
		} finally {
			String payload = "{\n" +
				"\t\"log-errors\": true,\n" +
				"\t\"debug-allow\": true,\n" +
				"\t\"profile-allow\": true\n" +
				"}";

			mgr.updateTaskServer("TaskServer", payload);
			Fragment xml = mgr.getPropertiesAsXml();
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:log-errors"));
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:debug-allow"));
			assertEquals("true", xml.getElementValue("/m:task-server-properties/m:profile-allow"));
		}
	}
}
