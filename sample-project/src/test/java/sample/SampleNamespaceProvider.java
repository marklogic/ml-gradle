package sample;

import java.util.List;

import org.jdom2.Namespace;

import com.marklogic.junit.MarkLogicNamespaceProvider;

/**
 * A project will typically define a subclass of MarkLogicNamespaceProvider that then registers namespaces specific to
 * the project. The prefixes for these namespaces can then be used in XPath expressions on Fragment instances. An
 * instance of this class is then returned by the abstract base test class for the project - in this case,
 * AbstractSampleProjecTest - as the namespace provider for tests.
 */
public class SampleNamespaceProvider extends MarkLogicNamespaceProvider {

    @Override
    protected List<Namespace> buildListOfNamespaces() {
        List<Namespace> list = super.buildListOfNamespaces();
        list.add(Namespace.getNamespace("sample", "http://marklogic.com/sample"));
        return list;
    }

}
