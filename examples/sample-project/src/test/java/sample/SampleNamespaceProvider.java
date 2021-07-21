package sample;

import com.marklogic.junit5.MarkLogicNamespaceProvider;

/**
 * A project will typically define a subclass of MarkLogicNamespaceProvider that then registers namespaces specific to
 * the project. The prefixes for these namespaces can then be used in XPath expressions on Fragment instances. An
 * instance of this class is then returned by the abstract base test class for the project - in this case,
 * AbstractSampleProjectTest - as the namespace provider for tests.
 */
public class SampleNamespaceProvider extends MarkLogicNamespaceProvider {

	public SampleNamespaceProvider(String... additionalPrefixesAndUris) {
		super(additionalPrefixesAndUris);
		addNamespace("sample", "http://marklogic.com/sample");
	}


}
