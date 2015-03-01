package com.marklogic.appdeployer;

/**
 * I think these interface methods should assume config has already been set on the implementation. The impl can always
 * expose something like a RestTemplate so the client can make any REST API calls that they'd like, in case these
 * methods don't accomplish what the client wants.
 */
public interface AppDeployer {

    public void installPackages();

    public void uninstallApp();

    public void mergeDatabasePackages();

    public void mergeHttpServerPackages();

}
