package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

class CreateRoleTask extends SecurityTask {

    String roleName
    String roleDescription
    String[] roleNames
    String[] permissionRoles
    String[] permissionCapabilities
    String[] collections

    String[] executePrivilegeActions
    String[] uriPrivilegeActions

    boolean removeRole = true

    @TaskAction
    void createRole() {
        SecurityHelper h = getSecurityHelper()

        if (removeRole) {
            h.removeRoles(roleName)
        }

        h.createRole(roleName, roleDescription, roleNames, permissionRoles, permissionCapabilities, collections)

        if (executePrivilegeActions != null) {
            for (String action : executePrivilegeActions) {
                h.setPrivilegeForRole(roleName, action, "execute")
            }
        }
        
        if (uriPrivilegeActions != null) {
            for (String action : uriPrivilegeActions) {
                h.setPrivilegeForRole(roleName, action, "uri")
            }
        }
    }
}
