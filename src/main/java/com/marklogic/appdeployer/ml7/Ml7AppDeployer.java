package com.marklogic.appdeployer.ml7;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.ManageClient;
import com.marklogic.appdeployer.pkg.DatabasePackageMerger;
import com.marklogic.appdeployer.pkg.HttpServerPackageMerger;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.clientutil.LoggingObject;
import com.marklogic.clientutil.modulesloader.ModulesLoader;
import com.marklogic.clientutil.modulesloader.impl.DefaultExtensionLibraryDescriptorBuilder;
import com.marklogic.clientutil.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.xccutil.template.XccTemplate;

public class Ml7AppDeployer extends LoggingObject implements AppDeployer {

    private ManageClient manageClient;
    private ModulesLoader modulesLoader;

    public Ml7AppDeployer(ManageClient manageClient) {
        this.manageClient = manageClient;
        modulesLoader = new DefaultModulesLoader();
    }

    @Override
    public void installPackages(AppConfig config) {
        String packageName = config.getPackageName();
        manageClient.deletePackage(packageName);
        manageClient.createPackage(packageName);

        installDatabases(config);
        createRestApiServers(config);
        installXdbcServers(config);

        logger.info("Finished installing packages for application: " + config.getName());
    }

    @Override
    public void uninstallApp(AppConfig appConfig) {
        String xquery = loadStringFromClassPath("uninstall-app.xqy");
        xquery = xquery.replace("%%APP_NAME%%", appConfig.getName());
        logger.info("Uninstalling app with name: " + appConfig.getName());
        try {
            executeXquery(appConfig, xquery);
        } catch (Exception e) {
            logger.warn("Could not uninstall app; it may not be installed yet? Cause: " + e.getMessage());
        }
    }

    @Override
    public void mergeDatabasePackages(AppConfig appConfig) {
        List<String> paths = appConfig.getDatabasePackageFilePaths();
        if (paths != null && !paths.isEmpty()) {
            File outputFile = new File(appConfig.getMergedDatabasePackageFilePath());
            if (outputFile.exists()) {
                logger.info("Deleting existing merged database package file: " + outputFile.getAbsolutePath());
                outputFile.delete();
            }

            logger.info("Merging database packages from paths: " + paths);
            String xml = new DatabasePackageMerger().mergeDatabasePackages(paths);
            File dir = outputFile.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
            try {
                FileCopyUtils.copy(xml, new FileWriter(outputFile));
                logger.info("Wrote merged database package file: " + appConfig.getMergedDatabasePackageFilePath());
                appConfig.setContentDatabaseFilePath(appConfig.getMergedDatabasePackageFilePath());
            } catch (IOException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    @Override
    public void mergeHttpServerPackages(AppConfig appConfig) {
        List<String> paths = appConfig.getHttpServerPackageFilePaths();
        if (paths != null && !paths.isEmpty()) {
            File outputFile = new File(appConfig.getMergedHttpServerPackageFilePath());
            if (outputFile.exists()) {
                logger.info("Deleting existing merged http server package file: " + outputFile.getAbsolutePath());
                outputFile.delete();
            }

            logger.info("Merging HTTP server packages from paths: " + paths);
            String xml = new HttpServerPackageMerger().mergeHttpServerPackages(paths);
            File dir = outputFile.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
            try {
                FileCopyUtils.copy(xml, new FileWriter(outputFile));
                logger.info("Wrote merged HTTP server package file: " + appConfig.getMergedHttpServerPackageFilePath());
                appConfig.setHttpServerFilePath(appConfig.getMergedHttpServerPackageFilePath());
            } catch (IOException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    @Override
    public void updateContentDatabases(AppConfig appConfig) {
        installContentDatabases(appConfig);
        manageClient.installPackage(appConfig.getPackageName());
    }

    @Override
    public void updateHttpServers(AppConfig config) {
        mergeHttpServerPackages(config);

        String path = config.getHttpServerFilePath();
        String xml = loadStringFromFile(path);

        addServer(config, xml, config.getRestServerName(), config.getRestPort(), config.getContentDatabaseName());
        if (config.isTestPortSet()) {
            addServer(config, xml, config.getTestRestServerName(), config.getTestRestPort(),
                    config.getTestContentDatabaseName());
        }

        manageClient.installPackage(config.getPackageName());
    }

    @Override
    public void clearContentDatabase(AppConfig config, String collection) {
        String xquery = collection != null ? "xdmp:collection-delete('" + collection + "')"
                : "for $forest-id in xdmp:database-forests(xdmp:database()) return xdmp:forest-clear($forest-id)";
        logger.info("Clearing content: " + xquery);
        executeXquery(config, xquery);
    }

    @Override
    public void clearModulesDatabase(AppConfig config, String... excludeUris) {
        logger.info("Clearing modules database; first verifying that an XDBC server exists");
        if (!manageClient.xdbcServerExists(config.getXdbcServerName(), config.getGroupName())) {
            logger.info("Could not find XDBC server to use to clear modules database; perhaps the application has not been installed yet?");
            return;
        }

        String xquery = "xdmp:eval(\"for $uri in cts:uris((), (), cts:and-query(())) ";
        if (excludeUris != null && excludeUris.length > 0) {
            String expr = " where fn:not($uri = (";
            for (int i = 0; i < excludeUris.length; i++) {
                if (i > 1) {
                    expr += ", ";
                }
                expr += "'" + excludeUris[i] + "'";
            }
            expr += "))";
            xquery += expr;
        }
        xquery += "return xdmp:document-delete($uri)\"";
        xquery += ", (), <options xmlns='xdmp:eval'><database>{xdmp:modules-database()}</database></options>)";
        try {
            executeXquery(config, xquery);
        } catch (Exception e) {
            logger.warn("Unable to clear the modules database; it may not exist yet? Cause: " + e.getMessage());
        }
    }

    @Override
    public void loadModules(AppConfig config, String assetRolesAndCapabilities) {
        DatabaseClient dbClient = newDatabaseClient(config);
        if (assetRolesAndCapabilities != null && modulesLoader instanceof DefaultModulesLoader) {
            logger.info("Will load assets with roles and capabilities: " + assetRolesAndCapabilities);
            ((DefaultModulesLoader) modulesLoader)
                    .setExtensionLibraryDescriptorBuilder(new DefaultExtensionLibraryDescriptorBuilder(
                            assetRolesAndCapabilities));
        }

        List<String> paths = config.getModulePaths();
        logger.info("Module paths: " + paths);

        try {
            for (String path : paths) {
                logger.info("Loading modules from: " + path);
                modulesLoader.loadModules(new File(path), dbClient);
            }
        } finally {
            dbClient.release();
        }
    }

    protected DatabaseClient newDatabaseClient(AppConfig config) {
        return DatabaseClientFactory.newClient(config.getHost(), config.getRestPort(), config.getUsername(),
                config.getPassword(), Authentication.DIGEST);
    }

    protected void installDatabases(AppConfig appConfig) {
        boolean installPackage = false;
        if (new File(appConfig.getTriggersDatabaseFilePath()).exists()) {
            addDatabase(appConfig, appConfig.getTriggersDatabaseFilePath(), appConfig.getTriggersDatabaseName());
            installPackage = true;
        }

        if (new File(appConfig.getSchemasDatabaseFilePath()).exists()) {
            addDatabase(appConfig, appConfig.getSchemasDatabaseFilePath(), appConfig.getSchemasDatabaseName());
            installPackage = true;
        }

        if (installContentDatabases(appConfig)) {
            installPackage = true;
        }

        if (installPackage) {
            manageClient.installPackage(appConfig.getPackageName());
        }
    }

    protected boolean installContentDatabases(AppConfig appConfig) {
        mergeDatabasePackages(appConfig);

        if (new File(appConfig.getContentDatabaseFilePath()).exists()) {
            addDatabase(appConfig, appConfig.getContentDatabaseFilePath(), appConfig.getContentDatabaseName());
            if (appConfig.isTestPortSet()) {
                addDatabase(appConfig, appConfig.getContentDatabaseFilePath(), appConfig.getTestContentDatabaseName());
            }
            return true;
        }

        return false;
    }

    protected void createRestApiServers(AppConfig appConfig) {
        // Don't need to pass in a modules database names, the REST API will create it automatically with the name we
        // want
        logger.info("Creating main REST API server with name: " + appConfig.getRestServerName());
        manageClient.createRestApiServer(appConfig.getRestServerName(), appConfig.getContentDatabaseName(),
                appConfig.getRestPort(), null);

        if (appConfig.isTestPortSet()) {
            logger.info("Creating test REST API server with name: " + appConfig.getTestRestServerName());
            manageClient.createRestApiServer(appConfig.getRestServerName(), appConfig.getTestContentDatabaseName(),
                    appConfig.getTestRestPort(), appConfig.getModulesDatabaseName());
        }
    }

    protected void installXdbcServers(AppConfig appConfig) {
        boolean installPackage = false;

        if (appConfig.getXdbcPort() != null && appConfig.getXdbcPort() > 0) {
            addXdbcServer(appConfig, appConfig.getXdbcServerName(), appConfig.getXdbcPort(),
                    appConfig.getContentDatabaseName());
            installPackage = true;
        }

        if (appConfig.isTestPortSet()) {
            addXdbcServer(appConfig, appConfig.getTestXdbcServerName(), appConfig.getTestXdbcPort(),
                    appConfig.getTestContentDatabaseName());
            installPackage = true;
        }

        if (appConfig.getModulesXdbcPort() != null && appConfig.getModulesXdbcPort() > 0) {
            addXdbcServer(appConfig, appConfig.getModulesXdbcServerName(), appConfig.getModulesXdbcPort(),
                    appConfig.getModulesDatabaseName());
            installPackage = true;
        }

        if (installPackage) {
            manageClient.installPackage(appConfig.getPackageName());
        }
    }

    protected void addXdbcServer(AppConfig appConfig, String serverName, Integer serverPort, String databaseName) {
        String xml = null;
        String file = appConfig.getXdbcServerFilePath();
        if (file != null && new File(file).exists()) {
            xml = loadStringFromFile(file);
        } else {
            xml = loadStringFromClassPath("xdbc-server-template.xml");
        }
        addServer(appConfig, xml, serverName, serverPort, databaseName);
    }

    protected void addServer(AppConfig appConfig, String xml, String serverName, Integer serverPort, String databaseName) {
        xml = replaceTokensInServerPackage(appConfig, xml, serverName, serverPort, databaseName);
        if (logger.isDebugEnabled()) {
            logger.debug("Server package XML:\n" + xml);
        }
        logger.info(String.format("Adding server %s in group %s to package %s", serverName, appConfig.getGroupName(),
                appConfig.getPackageName()));
        manageClient.addServer(appConfig.getPackageName(), serverName, appConfig.getGroupName(), xml);
    }

    protected String replaceTokensInServerPackage(AppConfig appConfig, String xml, String serverName,
            Integer serverPort, String databaseName) {
        xml = xml.replace("%%GROUP_NAME%%", appConfig.getGroupName());
        xml = xml.replace("%%SERVER_NAME%%", serverName);
        xml = xml.replace("%%PORT%%", serverPort.toString());
        xml = xml.replace("%%DATABASE_NAME%%", databaseName);
        xml = xml.replace("%%MODULES_DATABASE_NAME%%", appConfig.getModulesDatabaseName());
        return xml;
    }

    protected void addDatabase(AppConfig appConfig, String packageFilePath, String databaseName) {
        String xml = loadStringFromFile(packageFilePath);
        xml = replaceTokensInDatabasePackage(appConfig, xml, databaseName);
        if (logger.isDebugEnabled()) {
            logger.debug("Database package XML:\n" + xml);
        }
        manageClient.addDatabase(appConfig.getPackageName(), databaseName, xml);
    }

    protected String replaceTokensInDatabasePackage(AppConfig appConfig, String xml, String databaseName) {
        xml = xml.replace("%%DATABASE_NAME%%", databaseName);
        xml = xml.replace("%%SCHEMAS_DATABASE_NAME%%", appConfig.getSchemasDatabaseName());
        xml = xml.replace("%%TRIGGERS_DATABASE_NAME%%", appConfig.getTriggersDatabaseName());
        return xml;
    }

    protected String loadStringFromFile(String path) {
        try {
            return FileCopyUtils.copyToString(new FileReader(path));
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
    }

    protected String loadStringFromClassPath(String path) {
        path = ClassUtils.addResourcePathToPackagePath(getClass(), path);
        try {
            return new String(FileCopyUtils.copyToByteArray(new ClassPathResource(path).getInputStream()));
        } catch (IOException ie) {
            throw new RuntimeException("Unable to load string from classpath resource at: " + path + "; cause: "
                    + ie.getMessage(), ie);
        }
    }

    protected void executeXquery(AppConfig config, String xquery) {
        XccTemplate t = new XccTemplate(config.getXccUrl());
        t.executeAdhocQuery(xquery);
    }
}
