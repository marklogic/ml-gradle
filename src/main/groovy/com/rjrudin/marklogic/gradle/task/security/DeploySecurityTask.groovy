package com.rjrudin.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.appdeployer.impl.SimpleAppDeployer
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeploySecurityTask extends MarkLogicTask {

    @TaskAction
    void deploySecurity() {
        List<Command> commands = getProject().property("mlSecurityCommands")
        SimpleAppDeployer deployer = new SimpleAppDeployer(getManageClient(), getAdminManager())
        deployer.setCommands(commands)
        deployer.deploy(getAppConfig())
    }
}
