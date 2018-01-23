package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.util.MapPropertiesSource;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ModulesLoader;
import com.marklogic.client.ext.modulesloader.ModulesManager;
import com.marklogic.client.ext.modulesloader.impl.*;
import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.ext.tokenreplacer.PropertiesSource;
import com.marklogic.client.ext.tokenreplacer.RoxyTokenReplacer;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import com.marklogic.xcc.template.XccTemplate;

import java.util.Map;
import java.util.Properties;
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
			TokenReplacer tokenReplacer = buildModuleTokenReplacer(appConfig);
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
		String xccUri = "xcc://%s:%s@%s:%d";
		xccUri = String.format(xccUri, appConfig.getRestAdminUsername(), appConfig.getRestAdminPassword(),
			appConfig.getHost(), appConfig.getRestPort());
		XccStaticChecker checker = new XccStaticChecker(new XccTemplate(xccUri));
		checker.setBulkCheck(appConfig.isBulkLoadAssets());
		checker.setCheckLibraryModules(appConfig.isStaticCheckLibraryAssets());
		return checker;
	}

	protected TokenReplacer buildModuleTokenReplacer(AppConfig appConfig) {
		DefaultTokenReplacer r = appConfig.isUseRoxyTokenPrefix() ? new RoxyTokenReplacer() : new DefaultTokenReplacer();
		final Map<String, String> customTokens = appConfig.getCustomTokens();
		if (customTokens != null) {
			r.addPropertiesSource(new MapPropertiesSource(customTokens));
		}

		if (appConfig.getModuleTokensPropertiesSources() != null) {
			for (PropertiesSource ps : appConfig.getModuleTokensPropertiesSources()) {
				r.addPropertiesSource(ps);
			}
		}

		return r;
	}
}
