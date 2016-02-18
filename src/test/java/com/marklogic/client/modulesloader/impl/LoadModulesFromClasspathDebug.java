package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * Program for manually testing loading modules from the classpath instead of from the filesystem. This uses the test
 * jar at ./lib/modules.jar, which is expected to be on the classpath.
 */
public class LoadModulesFromClasspathDebug {

    public static void main(String[] args) throws Exception {

        /**
         * Assumes that 8000 points to your Modules database, and thus modules will be loaded there.
         */
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "admin", "admin",
                Authentication.DIGEST);

        /**
         * Instantiate a DefaultModulesLoader with no XccAssetLoader provided. The latter currently only works for
         * modules on the filesystem.
         */
        DefaultModulesLoader l = new DefaultModulesLoader();

        /**
         * A ModulesManager isn't yet useful because it's used for recording the last-loaded timestamp for files, which
         * doesn't yet work for classpath resources.
         */
        l.setModulesManager(null);

        try {
            /**
             * Don't include "classpath:" on this! The method will do it for you. It needs to know the root path within
             * the classpath that you expect to find your modules.
             */
            l.loadClasspathModules("/ml-modules", client);
        } finally {
            client.release();
        }
    }
}
