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
package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.ext.modulesloader.impl.DefaultFileFilter;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager;
import com.marklogic.junit.BaseTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.PermissionsFragment;
import com.marklogic.junit.XmlHelper;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class LoadModulesTest extends AbstractAppDeployerTest {

	private XccTemplate modulesXccTemplate;

	@BeforeEach
	public void setup() {
		modulesXccTemplate = new XccTemplate(appConfig.getHost(), appConfig.getAppServicesPort(), appConfig.getRestAdminUsername(),
			appConfig.getRestAdminPassword(), appConfig.getModulesDatabaseName());
	}

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void loadModulesWithStaticCheck() {
		appConfig.getModulePaths().clear();
		appConfig.getModulePaths().add("src/test/resources/sample-app/static-check-modules");
		appConfig.setStaticCheckAssets(false);
		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

		// This should succeed because modules aren't statically checked
		appDeployer.deploy(appConfig);

		// Now load modules with static check enabled
		appConfig.setStaticCheckAssets(true);
		initializeAppDeployer(buildLoadModulesCommand());
		try {
			appDeployer.deploy(appConfig);
			fail("An error should have been thrown because /ext/bad.xqy failed static check");
		} catch (Exception ex) {
			String message = ex.getMessage();
			assertTrue(message.contains("Unexpected token syntax error"),
				"Loading modules with 2.11.0 of ml-javaclient-util defaults to bulk loading, so static checking should as well; message: " + message);
			assertTrue(message.contains("in /ext/bad.xqy, on line 2"));
		} finally {
			initializeAppDeployer(new DeployRestApiServersCommand(true));
		}
	}

	@Test
	public void customModuleTimestampsPath() {
		String path = "build/custom-path.properties";
		File customFile = new File(path);
		customFile.mkdirs();
		if (customFile.exists()) {
			customFile.delete();
		}

		appConfig.setModuleTimestampsPath("build/custom-path.properties");
		LoadModulesCommand command = new LoadModulesCommand();
		initializeAppDeployer(new DeployRestApiServersCommand(true), command);
		appDeployer.deploy(appConfig);
		assertTrue(customFile.exists(), "The custom file should have been created when the modules were loaded");

		DefaultModulesLoader loader = (DefaultModulesLoader) command.getModulesLoader();
		PropertiesModuleManager manager = (PropertiesModuleManager) loader.getModulesManager();
		assertEquals(appConfig.getHost(), manager.getHost(),
			"The host should have been set on the PropertiesModuleManager via DefaultModulesLoaderFactory " +
				"so that module timestamps account for the host");
	}

	@Test
	public void loadModulesFromMultiplePaths() {
		// Setting batch size just to verify that nothing blows up when doing so
		appConfig.setModulesLoaderBatchSize(1);
		appConfig.getModulePaths().add("src/test/resources/sample-app/other-modules");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
		appDeployer.deploy(appConfig);

		assertModuleExistsWithDefaultPermissions("sample-lib is loaded from /ext in the default path",
			"/ext/sample-lib.xqy");
		assertModuleExistsWithDefaultPermissions("other-lib.xqy is loaded from the path added at the start of the test",
			"/other-lib.xqy");
	}

	@Test
	public void loadModulesWithCustomPermissions() {
		appConfig.setModulePermissions(appConfig.getModulePermissions() + ",app-user,execute");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

		appDeployer.deploy(appConfig);

		PermissionsFragment perms = new BaseTestHelper().getDocumentPermissions("/ext/sample-lib.xqy", modulesXccTemplate);
		// Default permissions set by AppConfig
		perms.assertPermissionExists("rest-admin", "read");
		perms.assertPermissionExists("rest-admin", "update");
		perms.assertPermissionExists("rest-extension-user", "execute");

		// Custom permission
		perms.assertPermissionExists("app-user", "execute");
	}

	@Test
	public void loadModulesWithAssetFileFilterAndTokenReplacement() {
		appConfig.setAssetFileFilter(new TestFileFilter());

		/**
		 * Add a couple tokens to replace in the modules. It's still a good practice to ensure these tokens don't
		 * hit on anything accidentally, so their names are capitalized. But since the module token replacement
		 * follows the Roxy convention by default and prefixes properties with "@ml.", our modules then need
		 * "@ml.%%COLOR%%", for example.
		 */
		appConfig.setUseRoxyTokenPrefix(true);
		appConfig.getCustomTokens().put("COLOR", "red");
		appConfig.getCustomTokens().put("DESCRIPTION", "${COLOR} description");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
		appDeployer.deploy(appConfig);

		assertEquals("true", modulesXccTemplate.executeAdhocQuery("doc-available('/ext/lib/test.xqy')"));
		assertEquals("false", modulesXccTemplate.executeAdhocQuery("doc-available('/ext/lib/test2.xqy')"));

		String xml = modulesXccTemplate.executeAdhocQuery("doc('/ext/lib/test.xqy')");
		Fragment f = new XmlHelper().parse(xml);
		f.assertElementValue("/test/color", "red");
		f.assertElementValue("/test/description", "red description");
	}

	@Test
	public void testServerExists() {
		appConfig.getFirstConfigDir().setBaseDir(new File(("src/test/resources/sample-app/db-only-config")));
		appConfig.setTestRestPort(8541);
		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

		appDeployer.deploy(appConfig);

		String[] uris = new String[]{"/Default/sample-app/rest-api/options/sample-app-options.xml",
			"/Default/sample-app/rest-api/options/sample-app-options.xml"};
		for (String uri : uris) {
			assertEquals("true", modulesXccTemplate.executeAdhocQuery(format("doc-available('%s')", uri)));
		}
	}

	@Test
	public void deleteTestModules() {
		appConfig.setDeleteTestModules(true);
		appConfig.setDeleteTestModulesPattern("/ext/lib/*.xqy");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand(),
			new DeleteTestModulesCommand());
		appDeployer.deploy(appConfig);

		String xquery = "fn:count(cts:uri-match('/ext/**.xqy'))";
		assertEquals(1, Integer.parseInt(modulesXccTemplate.executeAdhocQuery(xquery)));
	}

	@Test
	public void includeModulesPattern() {
		appConfig.setModuleFilenamesIncludePattern(Pattern.compile(".*/ext.*"));

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
		deploySampleApp();

		String xquery = "fn:count(cts:uris((), (), cts:true-query()))";
		assertEquals(4, Integer.parseInt(modulesXccTemplate.executeAdhocQuery(xquery)),
			"Should have the 3 /ext modules plus the REST API properties file, which was loaded when the REST server was created");
	}

	@Test
	void uriPrefix() {
		appConfig.setModuleUriPrefix("/example/prefix ");
		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
		deploySampleApp();

		String[] uris = modulesXccTemplate
			.executeAdhocQuery("cts:uris((), (), cts:directory-query('/example/prefix/', 'infinity'))")
			.split("\n");

		assertEquals(3, uris.length, "The trailing space is expected to be trimmed off to avoid errors from a user " +
			"accidentally including a trailing space e.g. in their gradle.properties file");

		List<String> uriList = Arrays.asList(uris);
		assertTrue(uriList.contains("/example/prefix/ext/lib/test2.xqy"));
		assertTrue(uriList.contains("/example/prefix/ext/lib/test.xqy"));
		assertTrue(uriList.contains("/example/prefix/ext/sample-lib.xqy"));
	}

	private void assertModuleExistsWithDefaultPermissions(String message, String uri) {
		assertEquals("true", modulesXccTemplate.executeAdhocQuery(format("fn:doc-available('%s')", uri)), message);
		PermissionsFragment perms = new BaseTestHelper().getDocumentPermissions(uri, modulesXccTemplate);
		perms.assertPermissionExists("rest-admin", "read");
		perms.assertPermissionExists("rest-admin", "update");
		perms.assertPermissionExists("rest-extension-user", "execute");
	}
}

class TestFileFilter extends DefaultFileFilter {

	@Override
	public boolean accept(File f) {
		return !f.getName().equals("test2.xqy") && super.accept(f);
	}

}
