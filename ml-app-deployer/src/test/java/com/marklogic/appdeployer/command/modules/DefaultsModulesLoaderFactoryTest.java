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
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultsModulesLoaderFactoryTest extends AbstractAppDeployerTest {

	private DefaultModulesLoaderFactory factory = new DefaultModulesLoaderFactory();

	@Test
	public void useHost() {
		// This should be the default behavior
		DefaultModulesLoader loader = (DefaultModulesLoader) factory.newModulesLoader(appConfig);
		PropertiesModuleManager manager = (PropertiesModuleManager) loader.getModulesManager();
		assertEquals(appConfig.getHost(), manager.getHost());
	}

	@Test
	public void dontUseHost() {
		appConfig.setModuleTimestampsUseHost(false);

		DefaultModulesLoader loader = (DefaultModulesLoader) factory.newModulesLoader(appConfig);
		PropertiesModuleManager manager = (PropertiesModuleManager) loader.getModulesManager();
		assertNull(manager.getHost());
	}

	@Test
	void cascadeCollectionsAndPermissions() {
		DefaultModulesLoader loader = (DefaultModulesLoader) factory.newModulesLoader(appConfig);

		// Should default to false in the 4.x timeframe
		assertFalse(loader.getAssetFileLoader().isCascadeCollections());
		assertFalse(loader.getAssetFileLoader().isCascadePermissions());

		appConfig.setCascadeCollections(true);
		appConfig.setCascadePermissions(true);

		loader = (DefaultModulesLoader) factory.newModulesLoader(appConfig);
		assertTrue(loader.getAssetFileLoader().isCascadeCollections());
		assertTrue(loader.getAssetFileLoader().isCascadePermissions());
	}
}
