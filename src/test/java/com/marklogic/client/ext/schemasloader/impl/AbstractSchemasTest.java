package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.ext.AbstractIntegrationTest;
import org.junit.Before;

public abstract class AbstractSchemasTest extends AbstractIntegrationTest {

	/**
	 * Wipes out the Schemas database - it's assumed you're not using the Schemas database for
	 * anything besides ad hoc testing like this.
	 */
	@Before
	public void setup() {
		client = newClient("Schemas");
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
	}

}
