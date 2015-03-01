package com.marklogic.appdeployer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Namespace;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.ml7.Ml7AppDeployer;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.MarkLogicNamespaceProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.XmlHelper;

public class MergeHttpServersTest extends XmlHelper {

    @Test
    public void test() throws IOException {
        List<String> mergeFilePaths = new ArrayList<>();
        mergeFilePaths.add("src/test/resources/test-http-server.xml");

        AppConfig config = new AppConfig();
        config.setHttpServerPackageFilePaths(mergeFilePaths);
        Ml7AppDeployer sut = new Ml7AppDeployer(config, null);
        sut.mergeHttpServerPackages();

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

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new MarkLogicNamespaceProvider() {
            @Override
            protected List<Namespace> buildListOfNamespaces() {
                List<Namespace> list = super.buildListOfNamespaces();
                list.add(Namespace.getNamespace("srv", "http://marklogic.com/manage/package/servers"));
                return list;
            }

        };
    }
}
