/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
