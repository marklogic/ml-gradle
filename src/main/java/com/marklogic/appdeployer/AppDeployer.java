package com.marklogic.appdeployer;

/**
 * I think these interface methods should assume config has already been set on the implementation. The impl can always
 * expose something like a RestTemplate so the client can make any REST API calls that they'd like, in case these
 * methods don't accomplish what the client wants.
 */
public interface AppDeployer {

    public void installPackages(AppConfig config);

    public void uninstallApp(AppConfig config);

    public void mergeDatabasePackages(AppConfig config);

    public void mergeHttpServerPackages(AppConfig config);

    public void updateContentDatabases(AppConfig config);

    public void updateHttpServers(AppConfig config);

    public void clearContentDatabase(AppConfig config, String collection);

    public void clearModulesDatabase(AppConfig config, String... excludeUris);
    
    public void loadModules(AppConfig config, String assetRolesAndCapabilities);

}
