package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.ext.tokenreplacer.PropertiesSource;
import com.marklogic.client.ext.tokenreplacer.RoxyTokenReplacer;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ModulesLoader;
import com.marklogic.client.ext.modulesloader.ModulesManager;
import com.marklogic.client.ext.modulesloader.impl.*;
import com.marklogic.xcc.template.XccTemplate;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public class DefaultModulesLoaderFactory extends LoggingObject implements ModulesLoaderFactory {

	@Override
	public ModulesLoader newModulesLoader(AppConfig appConfig) {
		ModulesManager modulesManager = null;
		String path = appConfig.getModuleTimestampsPath();
		if (path != null) {
			modulesManager = new PropertiesModuleManager(new File(path));
		}

		DatabaseClient modulesDatabaseClient = appConfig.newModulesDatabaseClient();
		AssetFileLoader assetFileLoader = new AssetFileLoader(modulesDatabaseClient, modulesManager);

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

		if (appConfig.isReplaceTokensInModules()) {
			assetFileLoader.setTokenReplacer(buildModuleTokenReplacer(appConfig));
		}

		DefaultModulesLoader modulesLoader = new DefaultModulesLoader(assetFileLoader);
		modulesLoader.setModulesManager(modulesManager);

		if (appConfig.isStaticCheckAssets()) {
			modulesLoader.setStaticChecker(newStaticChecker(appConfig));
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
		if (customTokens != null && !customTokens.isEmpty()) {
			r.addPropertiesSource(new PropertiesSource() {
				@Override
				public Properties getProperties() {
					Properties p = new Properties();
					p.putAll(customTokens);
					return p;
				}
			});
		}

		if (appConfig.getModuleTokensPropertiesSources() != null) {
			for (PropertiesSource ps : appConfig.getModuleTokensPropertiesSources()) {
				r.addPropertiesSource(ps);
			}
		}

		return r;
	}
}
