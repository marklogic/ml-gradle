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
package com.marklogic.appdeployer.util;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.modules.DefaultModulesLoaderFactory;
import com.marklogic.appdeployer.command.modules.ModulesLoaderFactory;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ModulesFinder;
import com.marklogic.client.ext.modulesloader.ModulesLoader;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesFinder;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.mgmt.util.SystemPropertySource;

import java.util.List;

/**
 * This is a hacked together prototype of loading modules from within groovysh. The idea is that all the necessary
 * configuration for loading modules can be collected from system properties, which can be set by a tool like ml-gradle.
 * This class can then be created and started in the startup script for groovysh so that when the shell starts, this
 * class can load new/modified modules.
 */
public class ModulesWatcher extends LoggingObject implements Runnable {

	private long sleepTime = 1000;
	private AppConfig appConfig;
	private ModulesLoaderFactory modulesLoaderFactory;

	public ModulesWatcher(AppConfig appConfig) {
		this.appConfig = appConfig;
		this.modulesLoaderFactory = new DefaultModulesLoaderFactory();
	}

	public static void startFromSystemProps() {
		ModulesWatcher mw = new ModulesWatcher(new DefaultAppConfigFactory(new SystemPropertySource()).newAppConfig());
		new Thread(mw).start();
	}

	@Override
	public void run() {
		ModulesLoader loader = modulesLoaderFactory.newModulesLoader(appConfig);
		if (loader instanceof DefaultModulesLoader) {
			((DefaultModulesLoader) loader).setCatchExceptions(true);
		}

		DatabaseClient client = appConfig.newDatabaseClient();
		List<String> paths = appConfig.getModulePaths();
		ModulesFinder finder = new DefaultModulesFinder();
		while (true) {
			for (String modulesPath : paths) {
				loader.loadModules(modulesPath, finder, client);
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ie) {
				// Ignore
			}
		}
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public ModulesLoaderFactory getModulesLoaderFactory() {
		return modulesLoaderFactory;
	}

	public void setModulesLoaderFactory(ModulesLoaderFactory modulesLoaderFactory) {
		this.modulesLoaderFactory = modulesLoaderFactory;
	}
}
