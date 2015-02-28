package com.marklogic.appdeployer.ml7;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.AppDeployer;
import com.marklogic.appdeployer.ManageClient;
import com.marklogic.appdeployer.ManageConfig;
import com.marklogic.clientutil.LoggingObject;

public class Ml7AppDeployer extends LoggingObject implements AppDeployer {

    private AppConfig appConfig;
    private ManageConfig manageConfig;
    private ManageClient manageClient;

    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        appConfig.setName("appdeployer");
        appConfig.setRestPort(8123);
        Ml7AppDeployer sut = new Ml7AppDeployer(appConfig);
        sut.installPackages();
    }

    public Ml7AppDeployer(AppConfig appConfig) {
        this(appConfig, new ManageConfig());
    }

    public Ml7AppDeployer(AppConfig appConfig, ManageConfig manageConfig) {
        this.appConfig = appConfig;
        this.manageConfig = manageConfig;
        this.manageClient = new Ml7ManageClient(manageConfig);
    }

    @Override
    public void installPackages() {
        String name = appConfig.getName();
        String packageName = name + "-package";
        manageClient.deletePackage(packageName);
        manageClient.createPackage(packageName);

        boolean installPackage = false;
        // boolean installTestResources = appConfig.isTestPortSet();

        if (new File(manageConfig.getTriggersDatabaseFilePath()).exists()) {
            manageClient.addDatabase(packageName, name + "-triggers", manageConfig.getTriggersDatabaseFilePath());
            installPackage = true;
        }

        if (new File(manageConfig.getSchemasDatabaseFilePath()).exists()) {
            manageClient.addDatabase(packageName, name + "-schemas", manageConfig.getSchemasDatabaseFilePath());
            installPackage = true;
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

    public ManageConfig getManageConfig() {
        return manageConfig;
    }
}
