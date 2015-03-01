package com.marklogic.gradle.task.manage

/**
 * Configuration data for talking to the Manage REST API.
 */
class ManageConfig {

    String host = "localhost"
    Integer port = 8002
    String username
    String password
}
