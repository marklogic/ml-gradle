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

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.modulesloader.ModulesLoader;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesFinder;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.ext.modulesloader.impl.TestServerModulesFinder;

import java.util.List;

/**
 * Command for loading modules via an instance of DefaultModulesLoader, which depends on an instance of XccAssetLoader -
 * these are all in the ml-javaclient-util library.
 */
public class LoadModulesCommand extends AbstractCommand {

	private ModulesLoader modulesLoader;
	private ModulesLoaderFactory modulesLoaderFactory;

	public LoadModulesCommand() {
		setExecuteSortOrder(SortOrderConstants.LOAD_MODULES);
		this.modulesLoaderFactory = new DefaultModulesLoaderFactory();
	}

	/**
	 * Public so that a client can initialize the ModulesLoader and then access it via the getter; this is useful for a
	 * tool like ml-gradle, where the ModulesLoader can be reused by multiple tasks.
	 *
	 * @param context
	 */
	public void initializeDefaultModulesLoader(CommandContext context) {
		logger.info("Initializing new instance of ModulesLoader");
		this.modulesLoader = modulesLoaderFactory.newModulesLoader(context.getAppConfig());
	}

	@Override
	public void execute(CommandContext context) {
		loadModulesIntoMainServer(context);

		if (context.getAppConfig().isTestPortSet()) {
			loadModulesIntoTestServer(context);
		}
	}

	/**
	 * If we have multiple module paths, we want to load via XCC the assets for each first, and then iterate over the
	 * paths again and load all the REST API resources. This ensures that if the REST server for loading REST API
	 * resources has a custom rewriter, it's guaranteed to be loaded before we try to load any REST API resources.
	 *
	 * @param context
	 */
	protected void loadModulesIntoMainServer(CommandContext context) {
		if (modulesLoader == null) {
			initializeDefaultModulesLoader(context);
		}

		AppConfig config = context.getAppConfig();
		DatabaseClient client = config.newDatabaseClient();

		final List<String> pathsList = config.getModulePaths();
		final String[] pathsArray = pathsList.toArray(new String[]{});

		try {
			logger.info("Loading modules from paths: " + pathsList);
			modulesLoader.loadModules(client, new DefaultModulesFinder(), pathsArray);
		} finally {
			client.release();
		}
	}

	/**
	 * We use a customized impl of DefaultModulesLoader here so we can ensure that options are always loaded again into
	 * the test server.
	 *
	 * @param context
	 */
	protected void loadModulesIntoTestServer(CommandContext context) {
		AppConfig config = context.getAppConfig();
		DatabaseClient client = config.newTestDatabaseClient();
		ModulesLoader testLoader = buildTestModulesLoader(context);
		try {
			logger.info("Loading modules into test server from paths: " + config.getModulePaths());
			testLoader.loadModules(client, new TestServerModulesFinder(), config.getModulePaths().toArray(new String[]{}));
		} finally {
			client.release();
		}
	}

	protected ModulesLoader buildTestModulesLoader(CommandContext context) {
		// Don't need an asset loader here, as only options/properties are loaded for the test server
		DefaultModulesLoader l = new DefaultModulesLoader();
		l.setModulesManager(null);
		return l;
	}

	public void setModulesLoader(ModulesLoader modulesLoader) {
		this.modulesLoader = modulesLoader;
	}

	public ModulesLoader getModulesLoader() {
		return modulesLoader;
	}

	public void setModulesLoaderFactory(ModulesLoaderFactory modulesLoaderFactory) {
		this.modulesLoaderFactory = modulesLoaderFactory;
	}

	public ModulesLoaderFactory getModulesLoaderFactory() {
		return modulesLoaderFactory;
	}
}
