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
