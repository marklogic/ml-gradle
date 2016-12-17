package com.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.modulesloader.ModulesLoader;
import com.marklogic.client.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.modulesloader.impl.PropertiesModuleManager;
import com.marklogic.client.modulesloader.impl.TestServerModulesFinder;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;

/**
 * Command for loading modules via an instance of DefaultModulesLoader, which depends on an instance of XccAssetLoader -
 * these are all in the ml-javaclient-util library.
 */
public class LoadModulesCommand extends AbstractCommand {

    private ModulesLoader modulesLoader;

    public LoadModulesCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_MODULES);
    }

    /**
     * Public so that a client can initialize the ModulesLoader and then access it via the getter; this is useful for a
     * tool like ml-gradle, where the ModulesLoader can be reused by multiple tasks.
     *
     * @param context
     */
    public void initializeDefaultModulesLoader(CommandContext context) {
        logger.info("Initializing instance of DefaultModulesLoader");
		DefaultModulesLoader l = new DefaultModulesLoader(context.getAppConfig().newXccAssetLoader());
		String path = context.getAppConfig().getModuleTimestampsPath();
		if (path != null) {
			l.setModulesManager(new PropertiesModuleManager(new File(path)));
		}
		l.setStaticChecker(context.getAppConfig().newStaticChecker());
        this.modulesLoader = l;
    }

    @Override
    public void execute(CommandContext context) {
        loadModulesIntoMainServer(context);

        if (context.getAppConfig().isTestPortSet()) {
            loadModulesIntoTestServer(context);
        }
    }

    /**
     * If we have multiple module paths, we want to load via XCC the assets for each first, and then iterate over the
     * paths again and load all the REST API resources. This ensures that if the REST server for loading REST API
     * resources has a custom rewriter, it's guaranteed to be loaded before we try to load any REST API resources.
     *
     * @param context
     */
    protected void loadModulesIntoMainServer(CommandContext context) {
        if (modulesLoader == null) {
            initializeDefaultModulesLoader(context);
        }

        AppConfig config = context.getAppConfig();
        DatabaseClient client = config.newDatabaseClient();

        try {
            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading asset modules from dir: " + modulesPath);
                modulesLoader.loadModules(new File(modulesPath), new AssetModulesFinder(), client);
            }

            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading all non-asset modules from dir: " + modulesPath);
                modulesLoader.loadModules(new File(modulesPath), new AllButAssetsModulesFinder(), client);
            }
        } finally {
            client.release();
        }
    }

    /**
     * We use a customized impl of DefaultModulesLoader here so we can ensure that options are always loaded again into
     * the test server.
     *
     * @param context
     */
    protected void loadModulesIntoTestServer(CommandContext context) {
        AppConfig config = context.getAppConfig();
        DatabaseClient client = config.newTestDatabaseClient();
        ModulesLoader testLoader = buildTestModulesLoader(context);
        try {
            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading modules into test server from dir: " + modulesPath);
                testLoader.loadModules(new File(modulesPath), new TestServerModulesFinder(), client);
            }
        } finally {
            client.release();
        }
    }

    protected ModulesLoader buildTestModulesLoader(CommandContext context) {
        // Don't need an asset loader here, as only options/properties are loaded for the test server
        DefaultModulesLoader l = new DefaultModulesLoader();
        l.setModulesManager(null);
        return l;
    }

    public void setModulesLoader(ModulesLoader modulesLoader) {
        this.modulesLoader = modulesLoader;
    }

    public ModulesLoader getModulesLoader() {
        return modulesLoader;
    }
}
