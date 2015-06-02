package com.marklogic.appdeployer;

public interface AppDeployer {

    public void deploy(AppConfig appConfig, ConfigDir configDir);

    public void undeploy(AppConfig appConfig, ConfigDir configDir);
}
