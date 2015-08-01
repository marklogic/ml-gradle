package com.marklogic.gradle.task.viewschemas

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask

class DeployViewSchemasTask extends MarkLogicTask {

    @TaskAction
    void deployViewSchemas() {
        List<Command> commands = getProject().property("mlViewCommands")
        SimpleAppDeployer deployer = new SimpleAppDeployer(getManageClient(), getAdminManager())
        deployer.setCommands(commands)
        deployer.deploy(getAppConfig())
    }
}
