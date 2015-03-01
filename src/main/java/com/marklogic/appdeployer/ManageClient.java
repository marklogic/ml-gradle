package com.marklogic.appdeployer;

/**
 * Interface for talking to /manage/* endpoints (presumably on port 8002).
 */
public interface ManageClient {

    public void deletePackage(String name);

    public void createPackage(String name);

    public void installPackage(String name);

    public void addDatabase(String packageName, String databaseName, String packageFilePath);

    public void createRestApiServer(String serverName, String database, Integer port, String modulesDatabase);

    public void addServer(String packageName, String serverName, String group, String packageXml);
    
    public boolean xdbcServerExists(String serverName, String group);
}
