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
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This uses the default Modules database, and it clears it out before the test starts.
 */
public class StaticCheckModulesTest extends AbstractIntegrationTest {

	private XccTemplate xccTemplate;
	private DefaultModulesLoader modulesLoader;
	private XccStaticChecker staticChecker;

	private String database = MODULES_DATABASE;
	private String dir = Paths.get("src", "test", "resources", "static-check").toString();

	@BeforeEach
	public void setup() {
		client = newClient(database);
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();

		xccTemplate = new XccTemplate(clientConfig.getHost(), clientConfig.getPort(), clientConfig.getUsername(),
			clientConfig.getPassword(), database);

		staticChecker = new XccStaticChecker(xccTemplate);

		AssetFileLoader assetFileLoader = new AssetFileLoader(client);
		modulesLoader = new DefaultModulesLoader(assetFileLoader);
		modulesLoader.setModulesManager(null);
		modulesLoader.setStaticChecker(staticChecker);
	}

	@Test
	public void staticCheckAndDontCheckLibraryModules() {
		staticChecker.setBulkCheck(false);
		staticChecker.setCheckLibraryModules(false);

		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(3, files.size(), "All 3 modules should have been loaded because we didn't static check the bad libary module");
	}

	@Test
	public void staticCheckAndCheckLibraryModules() {
		staticChecker.setBulkCheck(false);
		staticChecker.setCheckLibraryModules(true);

		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("The load should have failed because of the bad library module");
		} catch (RuntimeException ex) {
			System.out.println(ex.getMessage());
			assertTrue(ex.getMessage().contains("in /ext/bad-lib.xqy, on line 7"));
		}
	}

	@Test
	public void staticCheckAndCheckLibraryModulesAndCatchExceptions() {
		staticChecker.setBulkCheck(false);
		staticChecker.setCheckLibraryModules(true);
		modulesLoader.setCatchExceptions(true);

		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(3, files.size(), "The modules should have been loaded, and error messages logged but not thrown");
	}

	@Test
	public void bulkLoadAndStaticCheck() {
		staticChecker.setBulkCheck(true);
		staticChecker.setCheckLibraryModules(false);

		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(3, files.size(), "The load should have succeeded because we didn't check library modules");
	}

	@Test
	public void bulkLoadAndStaticCheckAndCheckLibraryModules() {
		staticChecker.setBulkCheck(true);
		staticChecker.setCheckLibraryModules(true);

		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("The load should have failed because of the bad library module");
		} catch (Exception ex) {
			assertTrue(ex.getMessage().contains("in /ext/bad-lib.xqy, on line 7"));
		}
	}

	@Test
	public void bulkLoadAndStaticCheckAndCheckLibraryModulesAndCatchExceptions() {
		staticChecker.setBulkCheck(true);
		staticChecker.setCheckLibraryModules(true);
		modulesLoader.setCatchExceptions(true);

		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(3, files.size(), "The modules should have been loaded, and error messages logged but not thrown");
	}
}
