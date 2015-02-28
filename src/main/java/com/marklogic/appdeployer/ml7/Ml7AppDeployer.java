package com.marklogic.appdeployer.ml7;

import java.io.File;

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
        String name = appConfig.getName();
        String packageName = appConfig.getPackageName();
        manageClient.deletePackage(packageName);
        manageClient.createPackage(packageName);

        boolean installPackage = false;
        boolean installTestResources = appConfig.isTestPortSet();

        if (new File(appConfig.getTriggersDatabaseFilePath()).exists()) {
            manageClient.addDatabase(packageName, name + "-triggers", appConfig.getTriggersDatabaseFilePath());
            installPackage = true;
        }

        if (new File(appConfig.getSchemasDatabaseFilePath()).exists()) {
            manageClient.addDatabase(packageName, name + "-schemas", appConfig.getSchemasDatabaseFilePath());
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

        manageClient.createRestApiServer(name, null, appConfig.getRestPort(), null);
    }

    protected String getPackageName() {
        return appConfig.getName() + "-package";
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
