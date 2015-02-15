package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction


class CreateUserTask extends SecurityTask {

    String username
    String userDescription
    String password
    String[] roleNames
    String[] collections
    String[] permissionRoles
    String[] permissionCapabilities
    boolean removeUser = true

    @TaskAction
    void executeXquery() {
        SecurityHelper h = getSecurityHelper()

        if (removeUser) {
            h.removeUsers(username)
        }

        println "Creating user ${username}"
        h.createUser(username, userDescription, password, roleNames, permissionRoles, permissionCapabilities, collections)
    }
}
