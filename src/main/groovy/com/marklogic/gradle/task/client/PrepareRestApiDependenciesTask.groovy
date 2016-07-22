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
class PrepareRestApiDependenciesTask extends MarkLogicTask {

	@TaskAction
	void prepareRestApiDependencies() {
		String configurationName = "mlRestApi"
		if (getProject().configurations.find { it.name == configurationName }) {
			Configuration config = getProject().getConfigurations().getAt(configurationName)
			if (config.files) {
				println "Found " + configurationName + " configuration, will unzip all of its dependencies to build/mlRestApi"

				def buildDir = new File("build/mlRestApi")
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
					println "Unzipping file: " + f.getAbsolutePath()
					ant.unzip(src: f, dest: buildDir, overwrite: "true")
				}

				List<String> modulePaths = getAppConfig().modulePaths
				List<String> newModulePaths = new ArrayList<String>()

				for (dir in buildDir.listFiles()) {
					if (dir.isDirectory()) {
						newModulePaths.add(new File(dir, "ml-modules").getAbsolutePath())
					}
				}

				// The config paths of the dependencies should be before the original config paths
				newModulePaths.addAll(modulePaths)
				getAppConfig().setModulePaths(newModulePaths)

				println "Finished unzipping mlRestApi dependencies; will now include modules at " + getAppConfig().modulePaths + "\n"
			}
		}
	}
}
