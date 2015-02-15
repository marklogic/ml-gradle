package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

class CreateDomainTask extends CpfTask {

    String domainName
    String domainDescription

    String scope
    String scopeUri
    String scopeDepth = "/"

    String modulesDatabaseName

    // assume modules database name and root of "/"
    
    // Some sensible defaults
    String[] pipelineNames = ["Status Change Handling", "Alerting"]
    String[] permissions = ["app-user", "read", "app-user", "execute"]

    @TaskAction
    void createDomain() {
        def dbName = modulesDatabaseName ? modulesDatabaseName : getAppConfig().name + "-modules"
        newCpfHelper().createDomain(domainName, domainDescription, scope, scopeUri, scopeDepth, dbName, pipelineNames, permissions)
        println "Created or updated domain: " + domainName + "\n"
    }
}
