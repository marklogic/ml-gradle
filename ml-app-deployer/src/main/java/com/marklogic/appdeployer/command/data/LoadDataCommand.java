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
package com.marklogic.appdeployer.command.data;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DataConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.file.FileLoader;
import com.marklogic.client.ext.file.GenericFileLoader;
import org.springframework.util.StringUtils;

import java.util.List;

public class LoadDataCommand extends AbstractCommand {

	public LoadDataCommand() {
		setExecuteSortOrder(SortOrderConstants.LOAD_DATA);
	}

	/**
	 * This should load to the content database by default, which is the final database in DHF. Can be overridden via
	 * database name.
	 */
	@Override
	public void execute(CommandContext context) {
		DataConfig dataConfig = context.getAppConfig().getDataConfig();
		if (dataConfig == null) {
			return;
		}

		if (!dataConfig.isDataLoadingEnabled()) {
			logger.info("Data loading is disabled");
			return;
		}

		List<String> dataPaths = dataConfig.getDataPaths();
		if (dataPaths == null || dataPaths.isEmpty()) {
			return;
		}

		final FileLoader fileLoader = buildFileLoader(context.getAppConfig());
		for (String dataPath : dataPaths) {
			fileLoader.loadFiles(dataPath);
		}
	}

	/**
	 * Build a FileLoader based on the configuration in the given AppConfig object.
	 *
	 * @param appConfig
	 * @return
	 */
	protected FileLoader buildFileLoader(AppConfig appConfig) {
		final DatabaseClient client = determineDatabaseClient(appConfig);
		final GenericFileLoader loader = new GenericFileLoader(client);

		loader.setCascadeCollections(appConfig.isCascadeCollections());
		loader.setCascadePermissions(appConfig.isCascadePermissions());

		DataConfig dataConfig = appConfig.getDataConfig();

		final Integer batchSize = dataConfig.getBatchSize();
		if (batchSize != null) {
			logger.info("Loading data in batches of size: " + batchSize);
			loader.setBatchSize(batchSize);
		}

		if (dataConfig.isReplaceTokensInData()) {
			loader.setTokenReplacer(appConfig.buildTokenReplacer());
		}

		loader.setAdditionalBinaryExtensions(appConfig.getAdditionalBinaryExtensions());
		loader.setPermissions(dataConfig.getPermissions());

		if (dataConfig.getFileFilter() != null) {
			loader.addFileFilter(dataConfig.getFileFilter());
		}

		if (dataConfig.getCollections() != null) {
			loader.setCollections(dataConfig.getCollections());
		}

		loader.setLogFileUris(dataConfig.isLogUris());

		return loader;
	}

	/**
	 * The assumption is that newDatabaseClient on the given AppConfig object specifies the connection to use for
	 * loading data. If databaseName is set on the DataConfig object belonging to the AppConfig object, then
	 * a connection is made to that database via the App-Services port configured on the AppConfig object.
	 *
	 * @param appConfig
	 * @return
	 */
	protected DatabaseClient determineDatabaseClient(AppConfig appConfig) {
		DataConfig dataConfig = appConfig.getDataConfig();
		final String databaseName = dataConfig.getDatabaseName();
		if (StringUtils.hasText(databaseName)) {
			logger.info("Will load data via App-Services port into database: " + databaseName);
			return appConfig.newAppServicesDatabaseClient(databaseName);
		}
		return appConfig.newDatabaseClient();
	}
}
