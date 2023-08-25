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
package com.marklogic.client.ext.file;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CascadeCollectionsAndPermissionsTest extends AbstractIntegrationTest {

	final private String PARENT_COLLECTION = "ParentCollection";
	final private String CHILD_COLLECTION = "ChildCollection";

	@BeforeEach
	public void setup() {
		client = newClient(MODULES_DATABASE);
		DatabaseClient modulesClient = client;
		modulesClient.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
	}

	@Test
	public void parentWithBothProperties() {
		String directory = "src/test/resources/process-files/cascading-metadata-test/parent1-withCP";
		GenericFileLoader loader = new GenericFileLoader(client);
		loader.loadFiles(directory);

		verifyCollections( "/child1_1-noCP/test.json", PARENT_COLLECTION);
		verifyPermissions( "/child1_1-noCP/test.json", "rest-writer", "update");

		verifyCollections( "/child1_2-withCP/test.json", CHILD_COLLECTION);
		verifyPermissions( "/child1_2-withCP/test.json", "rest-reader", "read");

		verifyCollections( "/child3_1-withCP/grandchild3_1_1-noCP/test.json", CHILD_COLLECTION);
		verifyPermissions( "/child3_1-withCP/grandchild3_1_1-noCP/test.json", "rest-reader", "read");

		verifyCollections("/child1/child1.json", "ParentCollection");
		verifyPermissions("/child1/child1.json", "rest-writer", "update");

		verifyCollections("/child2/child2.json", "child2");
		verifyPermissions("/child2/child2.json", "app-user", "read");

		verifyCollections("/parent.json", "ParentCollection");
		verifyPermissions("/parent.json", "rest-writer", "update");
	}

	@Test
	public void parentWithNoProperties() {
		String directory = "src/test/resources/process-files/cascading-metadata-test/parent2-noCP";
		GenericFileLoader loader = new GenericFileLoader(client);
		loader.loadFiles(directory);

		verifyCollections( "/child2_1-withCP/test.json", CHILD_COLLECTION);
		verifyPermissions( "/child2_1-withCP/test.json", "rest-reader", "read");

		verifyCollections( "/child2_2-noCP/test.json");
		verifyPermissions( "/child2_2-noCP/test.json");

		verifyCollections( "/child2_3-withCnoP/test.json", PARENT_COLLECTION);
		verifyPermissions( "/child2_3-withCnoP/test.json");

		verifyCollections( "/child2_3-withCnoP/grandchild2_3_1-withPnoC/test.json", PARENT_COLLECTION);
		verifyPermissions( "/child2_3-withCnoP/grandchild2_3_1-withPnoC/test.json", "rest-reader", "read");
	}
}
