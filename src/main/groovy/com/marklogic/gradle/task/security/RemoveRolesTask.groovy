package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

class RemoveRolesTask extends SecurityTask {

    String[] roles
    
    @TaskAction
    void removeRoles() {
        getSecurityHelper().removeRoles(roles)
        println "Finished removing roles ${roles}\n"
    }
}
