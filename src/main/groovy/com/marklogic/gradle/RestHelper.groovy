package com.marklogic.gradle

import org.gradle.api.Project;

import com.marklogic.gradle.template.XdbcServerTemplate;

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

class RestHelper {

    String url
    String username
    String password
    
    void deletePackage(String packageName) {
        println "\nDeleting package with " + packageName
        invoke("DELETE", "/manage/v2/packages/${packageName}")
    }

    void createPackage(String packageName, String format) {
        // TODO This is a sloppy way of testing whether the package exists already
        try {
            invoke("GET", "/manage/v2/packages/${packageName}?format=json")
            println "\nPackage already exists with name " + packageName
        } catch (Exception e) {
            println "\nCreating package " + packageName
            invoke("POST", "/manage/v2/packages?format=${format}&pkgname=${packageName}")
        }
    }

    void installPackage(String packageName, String format) {
        println "\nInstalling package " + packageName
        invoke("POST", "/manage/v2/packages/${packageName}/install?format=${format}")
    }
    
    void addDatabase(String packageName, String databaseName, String packageFilePath, String format) {
        String xml = new File(packageFilePath).text
        xml = xml.replace("%%DATABASE_NAME%%", databaseName)
        println "\nAdding database ${databaseName} to package ${packageName}"
        invoke("POST", "/manage/v2/packages/${packageName}/databases/${databaseName}?format=${format}", xml, "application/xml")
    }
    
    void updateDatabase(String packageName, String databaseName, String packageFilePath) {
        addDatabase(packageName, databaseName, packageFilePath, "json")
    }
    
    void updateDatabase(String packageName, String databaseName, String packageFilePath, String format) {
        addDatabase(packageName, databaseName, packageFilePath, format)
        installPackage(packageName, format)
    }
    
    void addHttpServerFromFile(String packageName, String groupName, String serverName, Integer port, String packageFilePath) {
        println "\nUsing HTTP package at file path ${packageFilePath}"
        String xml = new File(packageFilePath).text
        xml = xml.replace("%%GROUP_NAME%%", groupName)
        xml = xml.replace("%%SERVER_NAME%%", serverName)
        xml = xml.replace("%%PORT%%", port + "")
        println "Adding server ${serverName} in group ${groupName} to package ${packageName}"
        invoke("POST", "/manage/v2/packages/${packageName}/servers/${serverName}?group-id=${groupName}", xml, "application/xml")
    }
    
    void addXdbcServer(String packageName, String groupName, String appName, String contentDatabaseName, Integer port, String packageFilePath) {
        String xml = null
        if (packageFilePath && new File(packageFilePath).exists()) {
            println "\nUsing XDBC package at file path ${packageFilePath}"
            xml = new File(packageFilePath).text
        } else {
            xml = XdbcServerTemplate.TEMPLATE
        }
        
        String serverName = contentDatabaseName + "-xdbc"
        xml = xml.replace("%%GROUP_NAME%%", groupName)
        xml = xml.replace("%%SERVER_NAME%%", serverName)
        xml = xml.replace("%%PORT%%", port.toString())
        xml = xml.replace("%%DATABASE_NAME%%", contentDatabaseName)
        xml = xml.replace("%%MODULES_DATABASE_NAME%%", appName + "-modules")
        
        println "\nAdding server ${serverName} in group ${groupName} to package ${packageName}"
        invoke("POST", "/manage/v2/packages/${packageName}/servers/${serverName}?group-id=${groupName}", xml, "application/xml")
    }
    
    void addModulesXdbcServer(String appName, Integer port) {
        addModulesXdbcServer(appName + "-package", "Default", appName, port, null)
    }
    
    // TODO Resolve duplication between this and addXdbcServer
    void addModulesXdbcServer(String packageName, String groupName, String appName, Integer port, String packageFilePath) {
        String xml = null
        if (packageFilePath && new File(packageFilePath).exists()) {
            println "\nUsing XDBC package at file path ${packageFilePath}"
            xml = new File(packageFilePath).text
        } else {
            xml = XdbcServerTemplate.TEMPLATE
        }
        
        String serverName = appName + "-modules-xdbc"
        xml = xml.replace("%%GROUP_NAME%%", groupName)
        xml = xml.replace("%%SERVER_NAME%%", serverName)
        xml = xml.replace("%%PORT%%", port.toString())
        xml = xml.replace("%%DATABASE_NAME%%", appName + "-modules")
        xml = xml.replace("%%MODULES_DATABASE_NAME%%", appName + "-modules")
        
        println "\nAdding server ${serverName} in group ${groupName} to package ${packageName}"
        invoke("POST", "/manage/v2/packages/${packageName}/servers/${serverName}?group-id=${groupName}", xml, "application/xml")
    }
    
    void installAsset(String assetPath, String requestContentType, String path, String format) {
        String assetText = new File(assetPath).text
        String url = "/v1/ext/" + path
        if (format) {
            url += "?format=" + format
        }
        println "\nInstalling asset at URL ${url}"
        invoke("PUT", url, assetText, requestContentType)
    }
    
    void deleteRestApi(String serverName, String group, boolean includeContent, boolean includeModules) {
        if (!restApiExists(serverName)) {
            println "Cannot delete REST API instance with name ${serverName}; no such instance found. This may be because the rewriter for the server does not have 'rest-api' in its path."
            return
        }
        String path = "/v1/rest-apis/" + serverName + "?"
        if (group) {
            path += "group=" + group
        }
        if (includeContent) {
            path += "&include=content"
        }
        if (includeModules) {
            path += "&include=modules"
        }
        println "Deleting REST API ${serverName}"
        invoke("DELETE", path)
    }
    
    boolean restApiExists(String serverName) {
        // TODO Would be better to call /v1/rest-apis and look for a server with the given name
        // Gotta figure out how to parse XML in Groovy to do that
        println "\nChecking to see if REST API with name ${serverName} exists..."
        try {
            invoke("GET", "/v1/rest-apis/" + serverName)
            return true
        } catch (Exception e) {
            if (!"Not Found".equals(e.message)) {
                throw e
            }
            return false
        }
    }
    
    void createRestApi(String serverName, String database, Integer port, String modulesDatabase) {
        if (restApiExists(serverName)) {
            println "REST API instance already exists with name " + serverName
            return
        }
        def body = '{rest-api: {name: "' + serverName  + '"';
        if (database) {
            body += ', database: "' + database + '"'
        }
        if (port) {
            body += ', port: ' + port
        }
        if (modulesDatabase) {
            body += ', modules-database: "' + modulesDatabase + '"'
        }
        body += "}}"
        
        println "Creating new REST API server: " + body
        invoke("POST", "/v1/rest-apis", body, "application/json")
    }
    
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
