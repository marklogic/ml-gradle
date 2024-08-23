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

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.database.ElementIndex;
import com.marklogic.mgmt.template.database.DatabaseTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WriteDatabaseTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		buildResourceAndDeploy(new DatabaseTemplateBuilder());

		Database db = api.db("CHANGEME-name-of-database");
		assertTrue(db.getEnabled());

		ElementIndex index = db.getRangeElementIndex().get(0);
		assertEquals("CHANGEME-name-of-element", index.getLocalname());
		assertEquals("CHANGEME-namespace-of-element", index.getNamespaceUri());
		assertEquals("http://marklogic.com/collation/", index.getCollation());
		assertFalse(index.getRangeValuePositions());
	}
}
