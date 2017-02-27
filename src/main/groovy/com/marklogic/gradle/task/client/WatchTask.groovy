package com.marklogic.gradle.task.client

import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.client.DatabaseClient
import com.marklogic.client.modulesloader.ModulesLoader
import com.marklogic.client.modulesloader.impl.DefaultModulesFinder
import com.marklogic.client.modulesloader.impl.DefaultModulesLoader
import com.marklogic.gradle.task.MarkLogicTask

/**
 * Runs an infinite loop, and each second, it loads any new/modified modules. Often useful to run with the Gradle "-i" flag
 * so you can see which modules are loaded.
 *
 * Depends on an instance of LoadModulesCommand being in the Gradle Project, which should have been placed there by
 * MarkLogicPlugin. This prevents this class from having to know how to construct a ModulesLoader.
 */
class WatchTask extends MarkLogicTask {

	long sleepTime = 1000

	@TaskAction
	void watchModules() {
		LoadModulesCommand command = null
		AppDeployer d = getAppDeployer()
		if (d instanceof SimpleAppDeployer) {
			command = d.getCommand("LoadModulesCommand")
		}
		if (command == null) {
			command = new LoadModulesCommand()
		}

		ModulesLoader loader = command.getModulesLoader()
		if (loader == null) {
			command.initializeDefaultModulesLoader(getCommandContext())
			loader = command.getModulesLoader()
		}

		if (loader instanceof DefaultModulesLoader) {
			DefaultModulesLoader dml = (DefaultModulesLoader) loader;
			dml.setCatchExceptions(true)
			dml.setShutdownTaskExecutorAfterLoadingModules(false)
		}

		List<String> paths = getAppConfig().getModulePaths()
		println "Watching modules in paths: " + paths

		DatabaseClient client = newClient()
		while (true) {
			for (String path : paths) {
				loader.loadModules(new File(path), new DefaultModulesFinder(), client);
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ie) {
				// Ignore
			}
		}
	}
}
