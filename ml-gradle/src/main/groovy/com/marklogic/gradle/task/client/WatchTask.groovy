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

import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.client.DatabaseClient
import com.marklogic.client.ext.modulesloader.ModulesLoader
import com.marklogic.client.ext.modulesloader.ModulesManager
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesFinder
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader
import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.bsh.This
import org.springframework.core.io.Resource

import java.util.function.Consumer

/**
 * Runs an infinite loop, and each second, it loads any new/modified modules. Often useful to run with the Gradle "-i" flag
 * so you can see which modules are loaded.
 *
 * Depends on an instance of LoadModulesCommand being in the Gradle Project, which should have been placed there by
 * MarkLogicPlugin. This prevents this class from having to know how to construct a ModulesLoader.
 */
class WatchTask extends MarkLogicTask {

	@Input
	long sleepTime = 1000

	// Hook for after one or more modules have been loaded
	@Input
	@Optional
	Consumer<Set<Resource>> onModulesLoaded

	// Hook for after zero or more modules have been loaded, and after onModulesLoader has been invoked
	@Input
	@Optional
	Consumer<ModuleWatchingContext> afterModulesLoadedCallback

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
			DefaultModulesLoader dml = (DefaultModulesLoader) loader

			/**
			 * This is unfortunately more complicated than it should be. catchExceptions doesn't really apply to
			 * REST modules because REST modules are loaded async - unless DefaultModulesLoader's thread count is set
			 * to 1, in which case catchExceptions suffices. rethrowRestModulesFailure was added in 3.7.0 of
			 * ml-javaclient-util to cause deployments to fail when REST modules fail to load, which is good default
			 * behavior but isn't good for this task.
			 */
			dml.setCatchExceptions(true)
			dml.setRethrowRestModulesFailure(false)

			dml.setShutdownTaskExecutorAfterLoadingModules(false)

			if (project.hasProperty("ignoreDirty") && "true".equals(project.property("ignoreDirty"))) {
				ModulesManager mgr = dml.getModulesManager()
				if (mgr instanceof PropertiesModuleManager) {
					println "Ignoring modules that need loading, will only load modules that are created/modified after this starts"
					((PropertiesModuleManager) mgr).setMinimumFileTimestampToLoad(System.currentTimeMillis())
				} else {
					println "Unable to apply ignoreDirty property; the underlying modules loader implementation does not support this feature."
				}
			}
		}

		List<String> paths = getAppConfig().getModulePaths()
		println "Watching modules in paths: " + paths

		DatabaseClient client = newClient()

		ModuleWatchingContext moduleWatchingContext = new ModuleWatchingContext(loader, getAppConfig(), client)

		while (true) {
			for (String path : paths) {
				Set<Resource> loadedModules = loader.loadModules(path, new DefaultModulesFinder(), client);
				if (onModulesLoaded != null && loadedModules != null && !loadedModules.isEmpty()) {
					onModulesLoaded.accept(loadedModules)
				}
			}

			if (afterModulesLoadedCallback != null) {
				afterModulesLoadedCallback.accept(moduleWatchingContext)
			}

			// Kept here for legacy purposes
			afterModulesLoaded()

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ie) {
				// Ignore
			}
		}
	}

	/**
	 * Exists primarily so that DHF's extension of this class can invoke DHF-specific logic for loading modules.
	 *
	 * Now deprecated in 3.16.3 in favor of setting afterModulesLoaderCallback
	 */
	@Deprecated
	void afterModulesLoaded() {

	}
}
