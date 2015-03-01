package com.marklogic.appdeployer;

import java.util.List;

import org.jdom2.Namespace;

import com.marklogic.junit.MarkLogicNamespaceProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.XmlHelper;

public class AbstractAppDeployerTest extends XmlHelper {

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new MarkLogicNamespaceProvider() {
            @Override
            protected List<Namespace> buildListOfNamespaces() {
                List<Namespace> list = super.buildListOfNamespaces();
                list.add(Namespace.getNamespace("db", "http://marklogic.com/manage/package/databases"));
                list.add(Namespace.getNamespace("srv", "http://marklogic.com/manage/package/servers"));
                return list;
            }

        };
    }

}
