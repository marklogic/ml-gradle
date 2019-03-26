package com.marklogic.gradle.task.client

import com.marklogic.gradle.task.MarkLogicTask
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
				println "Found " + configurationName + " configuration, will extract all of its dependencies to build/" + configurationName

				if ("mlRestApi".equals(configurationName)) {
					println "\nWARNING: mlRestApi is deprecated as of release 3.13.0, please use mlBundle instead, which is a drop-in replacement.\n"
				}
				def buildDir = new File("build/" + configurationName)
				buildDir.delete()
				buildDir.mkdirs()

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
					ant.unzip(src: f, dest: buildDir, overwrite: "true")
				}

				List<String> modulePaths = getAppConfig().getModulePaths()
				List<String> newModulePaths = new ArrayList<>()

				List<String> dataPaths = getAppConfig().getDataConfig().getDataPaths()
				List<String> newDataPaths = new ArrayList<>()

				List<String> pluginPaths = getAppConfig().getPluginConfig().getPluginPaths()
				List<String> newPluginPaths = new ArrayList<>()

				for (dir in buildDir.listFiles()) {
					if (dir.isDirectory()) {
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
					}
				}

				// The config paths of the dependencies should be before the original config paths
				println "Finished extracting " + configurationName + " dependencies"

				if (!newModulePaths.isEmpty()) {
					newModulePaths.addAll(modulePaths)
					getAppConfig().setModulePaths(newModulePaths)
					println "Module paths including mlBundle paths: " + getAppConfig().getModulePaths()
				}

				if (!newDataPaths.isEmpty()) {
					newDataPaths.addAll(dataPaths)
					getAppConfig().getDataConfig().setDataPaths(newDataPaths)
					println "Data paths including mlBundle paths: " + getAppConfig().getDataConfig().getDataPaths()
				}

				if (!newPluginPaths.isEmpty()) {
					newPluginPaths.addAll(pluginPaths)
					getAppConfig().getPluginConfig().setPluginPaths(newPluginPaths)
					println "Plugin paths include mlBundle paths: " + getAppConfig().getPluginConfig().getPluginPaths()
				}
			}
		}
	}

}
