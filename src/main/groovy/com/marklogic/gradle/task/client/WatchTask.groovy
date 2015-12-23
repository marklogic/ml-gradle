package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.client.DatabaseClient
import com.marklogic.client.modulesloader.ModulesLoader
import com.marklogic.client.modulesloader.impl.DefaultModulesFinder
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
    public void watchModules() {
        LoadModulesCommand command = getProject().property("mlLoadModulesCommand")
        ModulesLoader loader = command.getModulesLoader()

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
