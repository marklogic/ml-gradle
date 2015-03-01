package com.marklogic.appdeployer.ml7;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.junit.Fragment;

public class ReplaceTokensInDatabasePackageTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        AppConfig config = new AppConfig("src/test/xqy");

        config.setName("tokentest");

        List<String> packagePaths = new ArrayList<>();
        packagePaths.add("src/test/xqy/packages/test-content-database.xml");
        packagePaths.add("src/test/xqy/packages/test-content-database2.xml");
        config.setDatabasePackageFilePaths(packagePaths);

        Ml7AppDeployer sut = new Ml7AppDeployer(null);
        sut.mergeDatabasePackages(config);

        assertEquals(
                "The merged database package file is expected to be in the build directory, which typically is not version-controlled",
                "build/ml-app-deployer/merged-database-package.xml", config.getContentDatabaseFilePath());

        String xml = sut.loadStringFromFile(config.getContentDatabaseFilePath());
        xml = sut.replaceTokensInDatabasePackage(config, xml, config.getContentDatabaseName());

        Fragment frag = parse(xml);
        frag.assertElementValue("//db:name", "tokentest-content");
        frag.assertElementValue("//db:forest-name", "tokentest-content-1");
        frag.assertElementValue("//db:schema-database", "tokentest-schemas");
        frag.assertElementValue("//db:triggers-database", "tokentest-triggers");
    }
}
