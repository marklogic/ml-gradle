package com.marklogic.gradle.task

import com.marklogic.appdeployer.impl.SimpleAppDeployer
import org.gradle.api.tasks.TaskAction

class DeployAppTask extends MarkLogicTask {

	/**
	 * Use "-Pignore" to specify the short class names of ml-app-deployer commands to ignore. The commands are then
	 * removed from the mlAppDeployer object, as long as it is an instance of SimpleAppDeployer from ml-app-deployer.
	 */
    @TaskAction
    void deployApp() {
	    def appDeployer = getAppDeployer()
	    if (project.hasProperty("ignore")) {
		    if (appDeployer instanceof SimpleAppDeployer) {
			    String[] commandNames = project.property("ignore").split(",")
			    SimpleAppDeployer deployer = (SimpleAppDeployer)appDeployer
			    for (String commandName : commandNames) {
				    def command = deployer.removeCommand(commandName)
				    if (command != null) {
					    println "Ignoring command: " + commandName
				    } else {
					    println "Could not find command specified by ignore property: " + commandName
				    }
			    }
		    }
		    else {
			    println "ignore property defined, but mlAppDeployer is not an instance of SimpleAppDeployer, so not able to ignore commands"
		    }
	    }
        appDeployer.deploy(getAppConfig())
    }
}
