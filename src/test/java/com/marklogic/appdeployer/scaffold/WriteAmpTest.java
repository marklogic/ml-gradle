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
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployAmpsCommand;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.template.security.AmpTemplateBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
