package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager;
import org.junit.Test;

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
}
