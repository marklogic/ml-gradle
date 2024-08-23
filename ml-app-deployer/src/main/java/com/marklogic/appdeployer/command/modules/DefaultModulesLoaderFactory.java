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
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ModulesLoader;
import com.marklogic.client.ext.modulesloader.ModulesManager;
import com.marklogic.client.ext.modulesloader.impl.*;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import com.marklogic.xcc.template.XccTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class DefaultModulesLoaderFactory extends LoggingObject implements ModulesLoaderFactory {

	@Override
	public ModulesLoader newModulesLoader(AppConfig appConfig) {
		/**
		 * Construct a DatabaseClient for loading non-REST extensions. This typically means connecting to the
		 * App-Services server on port 8000, which is likely to exist and support the client REST API (specifically,
		 * the /v1/documents endpoint).
		 *
		 * It however is not used for loading REST extensions. Because of how search options are loaded - their URI
		 * depends on the app server that they're loaded with - a DatabaseClient must be passed into the
		 * ModulesLoader method that's used for loading modules. This allows for the same ModulesLoader to be used for
		 * loading REST extensions against multiple REST servers.
		 */
		final DatabaseClient modulesDatabaseClient = appConfig.newModulesDatabaseClient();

		ModulesManager modulesManager = null;
		final String path = appConfig.getModuleTimestampsPath();
		if (path != null) {
			if (appConfig.isModuleTimestampsUseHost()) {
				modulesManager = new PropertiesModuleManager(path, modulesDatabaseClient);
			} else {
				modulesManager = new PropertiesModuleManager(path);
			}
		}

		final int threadCount = appConfig.getModulesLoaderThreadCount();

		RestBatchWriter assetBatchWriter = new RestBatchWriter(modulesDatabaseClient, false);
		assetBatchWriter.setThreadCount(threadCount);
		AssetFileLoader assetFileLoader = new AssetFileLoader(assetBatchWriter, modulesManager);
		assetFileLoader.setCascadeCollections(appConfig.isCascadeCollections());
		assetFileLoader.setCascadePermissions(appConfig.isCascadePermissions());
		if (appConfig.getModulesLoaderBatchSize() != null) {
			assetFileLoader.setBatchSize(appConfig.getModulesLoaderBatchSize());
		}

		final String permissions = appConfig.getModulePermissions();
		if (permissions != null) {
			assetFileLoader.setPermissions(permissions);
		}

		final String[] extensions = appConfig.getAdditionalBinaryExtensions();
		if (extensions != null) {
			assetFileLoader.setAdditionalBinaryExtensions(extensions);
		}

		if (appConfig.getAssetFileFilter() != null) {
			assetFileLoader.addFileFilter(appConfig.getAssetFileFilter());
		}

		if (StringUtils.isNotBlank(appConfig.getModuleUriPrefix())) {
			assetFileLoader.addDocumentFileProcessor(documentFile -> {
				documentFile.setUri(appConfig.getModuleUriPrefix().trim() + documentFile.getUri());
				return documentFile;
			});
		}

		final DefaultModulesLoader modulesLoader = new DefaultModulesLoader(assetFileLoader);

		if (appConfig.isReplaceTokensInModules()) {
			TokenReplacer tokenReplacer = appConfig.buildTokenReplacer();
			assetFileLoader.setTokenReplacer(tokenReplacer);
			modulesLoader.setTokenReplacer(tokenReplacer);
		}

		modulesLoader.setModulesManager(modulesManager);
		modulesLoader.setTaskThreadCount(threadCount);

		if (appConfig.isStaticCheckAssets()) {
			modulesLoader.setStaticChecker(newStaticChecker(appConfig));
		}

		Pattern modulesPattern = appConfig.getModuleFilenamesIncludePattern();
		if (modulesPattern != null) {
			modulesLoader.setIncludeFilenamePattern(modulesPattern);
		}

		return modulesLoader;
	}

	/**
	 * Currently only have an XCC implementation for static checking, as XCC gives much more useful error messages
	 * than REST does.
	 *
	 * @param appConfig
	 * @return
	 */
	protected StaticChecker newStaticChecker(AppConfig appConfig) {
		XccTemplate t = new XccTemplate(appConfig.getHost(), appConfig.getRestPort(), appConfig.getRestAdminUsername(),
			appConfig.getRestAdminPassword(), null);
		XccStaticChecker checker = new XccStaticChecker(t);
		checker.setBulkCheck(appConfig.isBulkLoadAssets());
		checker.setCheckLibraryModules(appConfig.isStaticCheckLibraryAssets());
		return checker;
	}
}
