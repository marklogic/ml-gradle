/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractSchemasTest extends AbstractIntegrationTest {

	/**
	 * Wipes out the Schemas database - it's assumed you're not using the Schemas database for
	 * anything besides ad hoc testing like this.
	 */
	@BeforeEach
	public void setup() {
		client = newClient("ml-javaclient-util-test-schemas");
		client.newServerEval().xquery("cts:uris((), (), cts:not-query(cts:collection-query('email-rules'))) ! xdmp:document-delete(.)").eval();
	}

}
