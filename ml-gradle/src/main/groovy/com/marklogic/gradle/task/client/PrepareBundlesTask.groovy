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
package com.marklogic.gradle.task.client

import com.marklogic.appdeployer.ConfigDir
import com.marklogic.gradle.task.MarkLogicTask
import org.apache.commons.io.FileUtils
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.project.DefaultAntBuilder
import org.gradle.api.internal.project.ant.AntLoggingAdapter
import org.gradle.api.tasks.TaskAction

/**
 * Purpose of this task is to unzip each restApi dependency to the build directory and then register the path of each
 * unzipped directory in AppConfig.modulePaths.
 */
class PrepareBundlesTask extends MarkLogicTask {

	@TaskAction
	void prepareBundles() {
		prepareBundlesForConfiguration("mlRestApi")
		prepareBundlesForConfiguration("mlBundle")
	}

	void prepareBundlesForConfiguration(configurationName) {
		if (getProject().configurations.find { it.name == configurationName }) {
			Configuration config = getProject().getConfigurations().getAt(configurationName)
			if (config.files) {

				getLogger().info("Found " + configurationName + " configuration, will extract all of its dependencies to build/" + configurationName)

				if ("mlRestApi".equals(configurationName)) {
					println "\nWARNING: mlRestApi is deprecated as of release 3.13.0, please use mlBundle instead, which is a drop-in replacement.\n"
				}
				
				def buildDir = new File(getProject().getProjectDir(), "build")
				buildDir.mkdirs()
				def bundleDir = new File(buildDir, configurationName)
				try {
					FileUtils.cleanDirectory(bundleDir)
				} catch (Exception e) {
					println "Unable to delete directory: " + bundleDir
				}
				bundleDir.mkdirs()

				// Constructing a DefaultAntBuilder seems to avoid Xerces-related classpath issues
				// The DefaultAntBuilder constructor changed between Gradle 2.13 and 2.14, and we want
				// to support both, so we try the 2.14 approach first and then 2.13
				def ant = null
				try {
					ant = new DefaultAntBuilder(getProject(), new AntLoggingAdapter())
				} catch (Exception ex) {
					ant = new DefaultAntBuilder(getProject())
				}

				for (f in config.files) {
					ant.unzip(src: f, dest: bundleDir, overwrite: "true")
				}

				List<ConfigDir> configDirs = getAppConfig().getConfigDirs()
				List<ConfigDir> newConfigDirs = new ArrayList<>()

				List<String> modulePaths = getAppConfig().getModulePaths()
				List<String> newModulePaths = new ArrayList<>()

				List<String> dataPaths = getAppConfig().getDataConfig().getDataPaths()
				List<String> newDataPaths = new ArrayList<>()

				List<String> pluginPaths = getAppConfig().getPluginConfig().getPluginPaths()
				List<String> newPluginPaths = new ArrayList<>()

				List<String> schemaPaths = getAppConfig().getSchemaPaths()
				List<String> newSchemaPaths = new ArrayList<>()

				for (dir in bundleDir.listFiles()) {
					if (dir.isDirectory()) {
						File configDir = new File(dir, "ml-config")
						if (configDir != null && configDir.exists()) {
							newConfigDirs.add(new ConfigDir(configDir))
						}

						File modulesDir = new File(dir, "ml-modules")
						if (modulesDir != null && modulesDir.exists()) {
							newModulePaths.add(modulesDir.getAbsolutePath())
						}

						File dataDir = new File(dir, "ml-data")
						if (dataDir != null && dataDir.exists()) {
							newDataPaths.add(dataDir.getAbsolutePath())
						}

						File pluginsDir = new File(dir, "ml-plugins")
						if (pluginsDir != null && pluginsDir.exists()) {
							newPluginPaths.add(pluginsDir.getAbsolutePath())
						}

						File schemasDir = new File(dir, "ml-schemas")
						if (schemasDir != null && schemasDir.exists()) {
							newSchemaPaths.add(schemasDir.getAbsolutePath())
						}
					}
				}

				// The config paths of the dependencies should be before the original config paths
				getLogger().info("Finished extracting " + configurationName + " dependencies")

				if (!newConfigDirs.isEmpty()) {
					newConfigDirs.addAll(configDirs)
					getAppConfig().setConfigDirs(newConfigDirs)
					getLogger().info("Config paths including mlBundle paths:")
					for (ConfigDir configDir : getAppConfig().getConfigDirs()) {
						getLogger().info(configDir.getBaseDir().getAbsolutePath())
					}
				}

				if (!newModulePaths.isEmpty()) {
					newModulePaths.addAll(modulePaths)
					getAppConfig().setModulePaths(newModulePaths)
					getLogger().info("Module paths including mlBundle paths: " + getAppConfig().getModulePaths())
				}

				if (!newDataPaths.isEmpty()) {
					newDataPaths.addAll(dataPaths)
					getAppConfig().getDataConfig().setDataPaths(newDataPaths)
					getLogger().info("Data paths including mlBundle paths: " + getAppConfig().getDataConfig().getDataPaths())
				}

				if (!newPluginPaths.isEmpty()) {
					newPluginPaths.addAll(pluginPaths)
					getAppConfig().getPluginConfig().setPluginPaths(newPluginPaths)
					getLogger().info("Plugin paths including mlBundle paths: " + getAppConfig().getPluginConfig().getPluginPaths())
				}

				if (!newSchemaPaths.isEmpty()) {
					newSchemaPaths.addAll(schemaPaths)
					getAppConfig().setSchemaPaths(newSchemaPaths)
					getLogger().info("Schema paths including mlBundle paths: " + getAppConfig().getSchemaPaths())
				}
			}
		}
	}

}
