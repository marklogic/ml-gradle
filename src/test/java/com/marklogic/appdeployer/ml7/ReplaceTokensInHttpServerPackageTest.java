package com.marklogic.appdeployer.ml7;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.junit.Fragment;

public class ReplaceTokensInHttpServerPackageTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        List<String> mergeFilePaths = new ArrayList<>();
        mergeFilePaths.add("src/test/xqy/packages/test-http-server.xml");

        AppConfig config = new AppConfig();
        config.setName("httptest");
        config.setRestPort(8888);
        config.setHttpServerPackageFilePaths(mergeFilePaths);

        Ml7AppDeployer sut = new Ml7AppDeployer(null);
        sut.mergeHttpServerPackages(config);

        assertEquals(
                "The merged server package file is expected to be in the build directory, which typically is not version-controlled",
                "build/ml-app-deployer/merged-http-server-package.xml", config.getHttpServerFilePath());

        String xml = sut.loadStringFromFile(config.getHttpServerFilePath());
        xml = sut.replaceTokensInServerPackage(config, xml, config.getRestServerName(), config.getRestPort(),
                config.getContentDatabaseName());

        Fragment frag = parse(xml);
        frag.assertElementValue("//srv:config/srv:group-name", "Default");
        frag.assertElementValue("//srv:config/srv:name", "httptest");
        frag.assertElementValue("//srv:port", "8888");
        frag.assertElementValue("//srv:links/srv:group-name", "Default");
        frag.assertElementValue("//srv:links/srv:database", "httptest-content");
        frag.assertElementValue("//srv:links/srv:modules-database", "httptest-modules");
    }
}
