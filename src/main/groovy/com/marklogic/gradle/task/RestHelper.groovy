package com.marklogic.gradle.task

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

/**
 * Simple utility class intended to simplify making ad hoc HTTP calls in a Gradle file.
 */
class RestHelper {

    String url
    String username
    String password

    HttpResponseDecorator invoke(String method, String path, String body, String requestContentType) {
        RESTClient client = buildClient(path)
        def params = [:]
        params.body = body
        params.requestContentType = requestContentType

        println "Sending a '$method' to '$client.uri'"
        return client."${method.toLowerCase()}"(params)
    }

    HttpResponseDecorator invoke(String method, String path) {
        RESTClient client = buildClient(path)
        println "Sending a '$method' to '$client.uri'"
        return client."${method.toLowerCase()}"([:])
    }

    RESTClient buildClient(String path) {
        RESTClient client = new RESTClient()
        client.getEncoder().putAt("application/xquery", client.getEncoder().getAt("text/plain"))

        client.uri = this.getUrl() + path
        client.auth.basic(this.getUsername(), this.getPassword())
        return client;
    }
}
