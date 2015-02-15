package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

class CreateDomainConfigurationTask extends CpfTask {

    String restartUser
    String modulesDatabaseName
    String defaultDomainName
    
    // Sensible default
    String[] permissions = ["app-user", "read", "app-user", "execute"]

    @TaskAction
    void createDomainConfiguration() {
        def dbName = modulesDatabaseName ? modulesDatabaseName : getAppConfig().name + "-modules"
        newCpfHelper().createDomainConfiguration(restartUser, dbName, defaultDomainName, permissions)
        println "Created or updated domain configuration\n"
    }
}
