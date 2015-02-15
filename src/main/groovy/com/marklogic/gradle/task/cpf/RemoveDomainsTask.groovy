package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

class RemoveDomainsTask extends CpfTask {

    String[] domainNames

    @TaskAction
    void removeDomains() {
        def h = newCpfHelper()
        for (String name : domainNames) {
            try {
                h.removeDomain(name)
                println "Removed CPF domain: " + name
            } catch (Exception ex) {
                println "Could not remove CPF domain: " + name + ", perhaps because it does not exist; cause: " + ex.getMessage()
            }
        }
        println "Completed removing CPF domains\n"
    }
}
