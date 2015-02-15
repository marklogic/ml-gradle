package com.marklogic.gradle

class AppConfig {

    String name = "sample-app"
    String host = "localhost"
    String username
    String password
    
    Integer restPort = 8100
    Integer xdbcPort = 8101
    Integer testRestPort
    Integer testXdbcPort
    Integer modulesXdbcPort
    
    String defaultModulePath = "src/main/xqy"
    List<String> modulePaths
    
    public AppConfig() {
        modulePaths = new ArrayList<String>()
        modulePaths.add(defaultModulePath)
    }
    
    public String getXccUrl() {
        return "xcc://${username}:${password}@${host}:${xdbcPort}"
    }
    
    public String getTestXccUrl() {
        return "xcc://${username}:${password}@${host}:${testXdbcPort}"
    }
    
    public String getModulesXccUrl() {
        return "xcc://${username}:${password}@${host}:${modulesXdbcPort}"
    }
}
