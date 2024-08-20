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


	private GenericFileLoader loader;

	@BeforeEach
	void setup() {
		client = newClient(MODULES_DATABASE);
		DatabaseClient modulesClient = client;
		modulesClient.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
		loader = new GenericFileLoader(client);
		loader.setCascadeCollections(true);
		loader.setCascadePermissions(true);
	}

	@Test
	void parentWithBothProperties() {
		loader.loadFiles("src/test/resources/process-files/cascading-metadata-test/parent1-withCP");

		verifyCollections("/parent.json", PARENT_COLLECTION);
		verifyPermissions("/parent.json", "rest-writer", "update");

		// Should be same as parent as it doesn't have C/P files.
		verifyCollections("/child1/child1.json", PARENT_COLLECTION);
		verifyPermissions("/child1/child1.json", "rest-writer", "update");

		// Differs from parent because it has its own C/P files.
		verifyCollections("/child2/child2.json", "child2");
		verifyPermissions("/child2/child2.json", "app-user", "read");

		// Differs from parent because it has its own C/P files.
		verifyCollections("/child3/child3.json", "child3");
		verifyPermissions("/child3/child3.json", "rest-reader", "read");

		// Should inherit from child3, not parent.
		verifyCollections("/child3/grandchild3/grandchild3.json", "child3");
		verifyPermissions("/child3/grandchild3/grandchild3.json", "rest-reader", "read");

		// Should inherit from parent.
		verifyCollections("/child4/child4.json", PARENT_COLLECTION);
		verifyPermissions("/child4/child4.json", "rest-writer", "update");

		// Should override parent.
		verifyCollections("/child4/grandchild4/grandchild4.json", "grandchild4");
		verifyPermissions("/child4/grandchild4/grandchild4.json", "qconsole-user", "read");
	}

	@Test
	void parentWithNoProperties() {
		loader.loadFiles("src/test/resources/process-files/cascading-metadata-test/parent2-noCP");

		verifyCollections("/child2_1-withCP/test.json", CHILD_COLLECTION);
		verifyPermissions("/child2_1-withCP/test.json", "rest-reader", "read");

		verifyCollections("/child2_2-noCP/test.json");
		verifyPermissions("/child2_2-noCP/test.json");

		verifyCollections("/child2_3-withCnoP/test.json", PARENT_COLLECTION);
		verifyPermissions("/child2_3-withCnoP/test.json");

		verifyCollections("/child2_3-withCnoP/grandchild2_3_1-withPnoC/test.json", PARENT_COLLECTION);
		verifyPermissions("/child2_3-withCnoP/grandchild2_3_1-withPnoC/test.json", "rest-reader", "read");
	}

	/**
	 * Verifies that by default, cascading is disabled. This is to preserve backwards compatibility in 4.x. We
	 * expect to change this for 5.0.
	 */
	@Test
	void cascadingDisabled() {
		loader = new GenericFileLoader(client);

		loader.loadFiles("src/test/resources/process-files/cascading-metadata-test/parent1-withCP");

		verifyCollections("/parent.json", PARENT_COLLECTION);
		verifyPermissions("/parent.json", "rest-writer", "update");

		// Has no C/P files.
		verifyCollections("/child1/child1.json");
		verifyPermissions("/child1/child1.json");

		// Has C/P files.
		verifyCollections("/child2/child2.json", "child2");
		verifyPermissions("/child2/child2.json", "app-user", "read");

		// Has C/P files.
		verifyCollections("/child3/child3.json", "child3");
		verifyPermissions("/child3/child3.json", "rest-reader", "read");

		// Has no C/P files.
		verifyCollections("/child3/grandchild3/grandchild3.json");
		verifyPermissions("/child3/grandchild3/grandchild3.json");

		// Has no C/P files.
		verifyCollections("/child4/child4.json");
		verifyPermissions("/child4/child4.json");

		// Has C/P files.
		verifyCollections("/child4/grandchild4/grandchild4.json", "grandchild4");
		verifyPermissions("/child4/grandchild4/grandchild4.json", "qconsole-user", "read");
	}
}
