package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.modulesloader.ModulesLoader;

/**
 * Interface for objects that can construct a ModulesLoader based on the configuration information in the given
 * AppConfig instance.
 */
public interface ModulesLoaderFactory {

	ModulesLoader newModulesLoader(AppConfig appConfig);
}
