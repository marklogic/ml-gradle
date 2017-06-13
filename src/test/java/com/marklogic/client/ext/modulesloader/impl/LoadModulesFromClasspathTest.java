package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.DatabaseClient;
import org.junit.After;
import org.junit.Test;

/**
 * Program for manually testing loading modules from the classpath instead of from the filesystem. This uses the test
 * jar at ./lib/modules.jar, which is expected to be on the classpath.
 */
public class LoadModulesFromClasspathTest extends AbstractIntegrationTest {

	private DatabaseClient modulesClient;

	@After
	public void teardown() {
		if (modulesClient != null) {
			modulesClient.release();
		}
	}

	@Test
	public void test() {
		newClient("Modules").newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
		modulesClient = client;
		client = newClient();

		DefaultModulesLoader l = new DefaultModulesLoader();

		/**
		 * A ModulesManager isn't yet useful because it's used for recording the last-loaded timestamp for files, which
		 * doesn't yet work for classpath resources.
		 */
		l.setModulesManager(null);

		/**
		 * Don't include "classpath:" on this! The method will do it for you. It needs to know the root path within
		 * the classpath that you expect to find your modules.
		 */
		l.loadClasspathModules("/ml-modules", client);

		String count = modulesClient.newServerEval().xquery("count(cts:uris((), (), cts:true-query()))").evalAs(String.class);
		assertEquals(17, Integer.parseInt(count));

	}
}
