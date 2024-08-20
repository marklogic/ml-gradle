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

import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.template.group.GroupTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
