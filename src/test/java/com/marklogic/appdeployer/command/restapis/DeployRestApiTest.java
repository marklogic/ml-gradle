package com.marklogic.appdeployer.command.restapis;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.DefaultPayloadTokenReplacer;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.Test;

public class DeployRestApiTest extends AbstractAppDeployerTest {

	@Test
	public void noRestApiFileAndNoContentDatabaseFile() throws Exception {
		final CommandContext context = new CommandContext(appConfig, null, null);

		DeployRestApiServersCommand command = new DeployRestApiServersCommand();
		String payload = command.getDefaultRestApiPayload(context);
		payload = new DefaultPayloadTokenReplacer().replaceTokens(payload, appConfig, false);
		ObjectNode node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(payload);
		assertFalse("forests-per-host shouldn't be set unless contentForestsPerHost is greater than zero",
			node.get("rest-api").has("forests-per-host"));

		appConfig.setContentForestsPerHost(2);

		payload = command.getDefaultRestApiPayload(context);
		payload = new DefaultPayloadTokenReplacer().replaceTokens(payload, appConfig, false);
		node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(payload);
		assertEquals("When contentForestsPerHost is set, its value should be included in the REST API payload",
			2, node.get("rest-api").get("forests-per-host").asInt());
	}
}
