package com.marklogic.appdeployer.ml7;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.ManageClient;
import com.marklogic.clientutil.LoggingObject;

public class Ml7AppDeployer extends LoggingObject implements AppDeployer {

    private AppConfig appConfig;
    private ManageClient manageClient;

    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        appConfig.setName("appdeployer");
        appConfig.setRestPort(8123);
        Ml7AppDeployer sut = new Ml7AppDeployer(appConfig, new Ml7ManageClient("localhost", 8002, "admin", "admin"));
        sut.installPackages();
    }

    public Ml7AppDeployer(AppConfig appConfig, ManageClient manageClient) {
        this.appConfig = appConfig;
        this.manageClient = manageClient;
    }

    @Override
    public void installPackages() {
        String packageName = appConfig.getPackageName();
        manageClient.deletePackage(packageName);
        manageClient.createPackage(packageName);

        boolean installPackage = false;
        boolean installTestResources = appConfig.isTestPortSet();

        if (new File(appConfig.getTriggersDatabaseFilePath()).exists()) {
            manageClient.addDatabase(packageName, appConfig.getTriggersDatabaseName(),
                    appConfig.getTriggersDatabaseFilePath());
            installPackage = true;
        }

        if (new File(appConfig.getSchemasDatabaseFilePath()).exists()) {
            manageClient.addDatabase(packageName, appConfig.getSchemasDatabaseName(),
                    appConfig.getSchemasDatabaseFilePath());
            installPackage = true;
        }

        if (new File(appConfig.getContentDatabaseFilePath()).exists()) {
            manageClient.addDatabase(packageName, appConfig.getContentDatabaseName(),
                    appConfig.getContentDatabaseFilePath());
            if (installTestResources) {
                manageClient.addDatabase(packageName, appConfig.getTestContentDatabaseName(),
                        appConfig.getContentDatabaseFilePath());
            }
        }

        if (installPackage) {
            manageClient.installPackage(packageName);
        }

        manageClient.createRestApiServer(appConfig.getRestServerName(), appConfig.getContentDatabaseName(),
                appConfig.getRestPort(), null);

        if (installTestResources) {
            manageClient.createRestApiServer(appConfig.getRestServerName(), appConfig.getTestContentDatabaseName(),
                    appConfig.getTestRestPort(), appConfig.getModulesDatabaseName());
        }

        if (appConfig.getModulesXdbcPort() != null && appConfig.getModulesXdbcPort() > 0) {

        }
    }

    public void addXdbcServer() {
        String xml = null;
        String file = appConfig.getXdbcServerFilePath();
        try {
            if (file != null && new File(file).exists()) {
                xml = FileCopyUtils.copyToString(new FileReader(file));
            } else {
                xml = FileCopyUtils.copyToString(new FileReader(new ClassPathResource(
                        "ml-appdeployer/xdbc-server-template.xml").getFile()));
            }
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }

        addServer(xml, appConfig.getXdbcServerName(), appConfig.getXdbcPort(), appConfig.getContentDatabaseName());
    }

    protected void addServer(String xml, String serverName, Integer serverPort, String databaseName) {
        xml = replaceTokensInServerPackageXml(xml, serverName, serverPort, databaseName);
        logger.info(String.format("Adding server %s in group %s to package %s", serverName, appConfig.getGroupName(),
                appConfig.getPackageName()));
        manageClient.addServer(appConfig.getPackageName(), serverName, appConfig.getGroupName(), xml);
    }

    protected String replaceTokensInServerPackageXml(String xml, String serverName, Integer serverPort,
            String databaseName) {
        xml = xml.replace("%%GROUP_NAME%%", appConfig.getGroupName());
        xml = xml.replace("%%SERVER_NAME%%", serverName);
        xml = xml.replace("%%PORT%%", serverPort.toString());
        xml = xml.replace("%%DATABASE_NAME%%", databaseName);
        xml = xml.replace("%%MODULES_DATABASE_NAME%%", appConfig.getName() + "-modules");
        return xml;
    }

    protected String getPackageName() {
        return appConfig.getName() + "-package";
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
