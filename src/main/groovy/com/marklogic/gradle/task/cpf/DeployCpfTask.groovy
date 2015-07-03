package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.cpf.CreateCpfConfigsCommand
import com.marklogic.appdeployer.command.cpf.CreateDomainsCommand
import com.marklogic.appdeployer.command.cpf.CreatePipelinesCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask

class DeployCpfTask extends MarkLogicTask {

    /**
     * For now, just assuming we can use new instances of the CPF commands. Would most likely be better to instead
     * reuse the instances that are in SimpleAppDeployer, but we don't yet have a 100% reliable way of finding those
     * instances - only way now is by checking for a parent class or a concrete class.
     */
    @TaskAction
    void deployCpf() {
        SimpleAppDeployer deployer = new SimpleAppDeployer(getManageClient(), getAdminManager(), new CreatePipelinesCommand(), new CreateDomainsCommand(), new CreateCpfConfigsCommand())
        deployer.deploy(getAppConfig())
    }
}
