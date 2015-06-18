package com.marklogic.appdeployer;

public interface AppDeployer {

    public void deploy(AppConfig appConfig);

    public void undeploy(AppConfig appConfig);
}
