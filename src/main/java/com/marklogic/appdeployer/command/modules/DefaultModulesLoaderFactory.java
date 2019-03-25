package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ModulesLoader;
import com.marklogic.client.ext.modulesloader.ModulesManager;
import com.marklogic.client.ext.modulesloader.impl.*;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import com.marklogic.xcc.template.XccTemplate;

import java.util.regex.Pattern;

public class DefaultModulesLoaderFactory extends LoggingObject implements ModulesLoaderFactory {

	@Override
	public ModulesLoader newModulesLoader(AppConfig appConfig) {
		ModulesManager modulesManager = null;
		String path = appConfig.getModuleTimestampsPath();
		if (path != null) {
			modulesManager = new PropertiesModuleManager(path);
		}

		int threadCount = appConfig.getModulesLoaderThreadCount();

		RestBatchWriter assetBatchWriter = new RestBatchWriter(appConfig.newModulesDatabaseClient(), false);
		assetBatchWriter.setThreadCount(threadCount);
		AssetFileLoader assetFileLoader = new AssetFileLoader(assetBatchWriter, modulesManager);
		if (appConfig.getModulesLoaderBatchSize() != null) {
			assetFileLoader.setBatchSize(appConfig.getModulesLoaderBatchSize());
		}

		String permissions = appConfig.getModulePermissions();
		if (permissions != null) {
			assetFileLoader.setPermissions(permissions);
		}

		String[] extensions = appConfig.getAdditionalBinaryExtensions();
		if (extensions != null) {
			assetFileLoader.setAdditionalBinaryExtensions(extensions);
		}

		if (appConfig.getAssetFileFilter() != null) {
			assetFileLoader.addFileFilter(appConfig.getAssetFileFilter());
		}

		DefaultModulesLoader modulesLoader = new DefaultModulesLoader(assetFileLoader);

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
