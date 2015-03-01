package com.marklogic.appdeployer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.ml7.Ml7AppDeployer;
import com.marklogic.junit.Fragment;

public class MergeHttpServersTest extends AbstractAppDeployerTest {

    @Test
    public void test() throws IOException {
        List<String> mergeFilePaths = new ArrayList<>();
        mergeFilePaths.add("src/test/xqy/packages/test-http-server.xml");

        AppConfig config = new AppConfig();
        config.setHttpServerPackageFilePaths(mergeFilePaths);
        Ml7AppDeployer sut = new Ml7AppDeployer(null);
        sut.mergeHttpServerPackages(config);

        String xml = new String(FileCopyUtils.copyToByteArray(new File(config.getHttpServerFilePath())));
        Fragment pkg = parse(xml);

        pkg.assertElementExists("/srv:package-http-server");
        pkg.assertElementExists("//srv:authentication[. = 'application-level']");
        pkg.assertElementExists("//srv:error-handler[. = '/MarkLogic/rest-api/error-handler.xqy']");
        pkg.assertElementExists("//srv:url-rewriter[. = '/MarkLogic/rest-api/rewriter.xqy']");
        pkg.assertElementExists("//srv:links/srv:modules-database[. = 'test-modules']");
        pkg.assertElementExists("//srv:links/srv:default-user[. = 'testuser']");

        pkg.prettyPrint();
    }
}
