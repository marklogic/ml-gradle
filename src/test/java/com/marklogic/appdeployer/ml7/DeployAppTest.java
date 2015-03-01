package com.marklogic.appdeployer.ml7;

import org.junit.Assert;
import org.junit.Test;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ml7.Ml7AppDeployer;
import com.marklogic.appdeployer.ml7.Ml7ManageClient;

public class DeployAppTest extends Assert {

    @Test
    public void deployToMl7() {
        AppConfig appConfig = new AppConfig();
        appConfig.setName("appdeployer");
        appConfig.setRestPort(8123);
        appConfig.setXdbcPort(8124);

        Ml7AppDeployer sut = new Ml7AppDeployer(new Ml7ManageClient("localhost", 8002, "admin", "admin"));

        sut.installPackages(appConfig);

        sut.uninstallApp(appConfig);
    }

}
