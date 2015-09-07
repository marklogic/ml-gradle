package com.rjrudin.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.client.DatabaseClient
import com.rjrudin.marklogic.appdeployer.AppDeployer
import com.rjrudin.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.rjrudin.marklogic.appdeployer.impl.SimpleAppDeployer
import com.rjrudin.marklogic.gradle.task.MarkLogicTask
import com.rjrudin.marklogic.modulesloader.impl.DefaultModulesLoader

/**
 * Runs an infinite loop, and each second, it loads any new/modified modules. Often useful to run with the Gradle "-i" flag
 * so you can see which modules are loaded.
 */
class WatchTask extends MarkLogicTask {

    long sleepTime = 1000

    @TaskAction
    public void watchModules() {
        println "Getting LoadModulesCommand from mlAppDeployer so it can be reused to load modules"
        AppDeployer d = getAppDeployer()
        if (d instanceof SimpleAppDeployer) {
            LoadModulesCommand command = d.getCommand("LoadModulesCommand")
            DefaultModulesLoader loader = new DefaultModulesLoader(command.newXccAssetLoader(getCommandContext()))
            List<String> paths = getAppConfig().getModulePaths()
            println "Watching modules in paths: " + paths

            DatabaseClient client = newClient()
            while (true) {
                for (String path : paths) {
                    loader.loadModules(new File(path), client);
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    // Ignore
                }
            }
        }
        else {
            throw new RuntimeException("Could not an instance of LoadModulesCommand in mlAppDeployer, thus not able to connect to MarkLogic to load modules")
        }
    }
}
