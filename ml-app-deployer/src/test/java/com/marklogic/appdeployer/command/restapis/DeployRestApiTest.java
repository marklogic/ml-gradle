/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.restapis;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.DefaultPayloadTokenReplacer;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DeployRestApiTest extends AbstractAppDeployerTest {

	@Test
	public void noRestApiFileAndNoContentDatabaseFile() throws Exception {
		final CommandContext context = new CommandContext(appConfig, null, null);

		DeployRestApiServersCommand command = new DeployRestApiServersCommand();
		String payload = command.getDefaultRestApiPayload(context);
		payload = new DefaultPayloadTokenReplacer().replaceTokens(payload, appConfig, false);
		ObjectNode node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(payload);
		assertFalse(node.get("rest-api").has("forests-per-host"),
			"forests-per-host shouldn't be set unless contentForestsPerHost is greater than zero");

		appConfig.setContentForestsPerHost(2);

		payload = command.getDefaultRestApiPayload(context);
		payload = new DefaultPayloadTokenReplacer().replaceTokens(payload, appConfig, false);
		node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(payload);
		assertEquals(2, node.get("rest-api").get("forests-per-host").asInt(),
			"When contentForestsPerHost is set, its value should be included in the REST API payload");
	}
}
