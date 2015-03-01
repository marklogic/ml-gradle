package com.marklogic.appdeployer;

/**
 * The intent of these methods is to provide high-level operations that abstract away one or more REST API calls for
 * installing and configuring an application. Each method should take an AppConfig instance, which provides general
 * application configuration data, and any parameters that are specific to the operation.
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
