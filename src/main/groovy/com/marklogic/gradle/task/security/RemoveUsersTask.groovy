package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

class RemoveUsersTask extends SecurityTask {

    String[] usernames

    @TaskAction
    void removeUsers() {
        getSecurityHelper().removeUsers(usernames)
        println "Finished removing users ${usernames}\n"
    }
}
