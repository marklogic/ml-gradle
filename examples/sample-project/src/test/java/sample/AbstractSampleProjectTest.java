package sample;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit5.NamespaceProvider;
import com.marklogic.junit5.spring.AbstractSpringMarkLogicTest;

/**
 * A project that uses marklogic-junit5 will usually have a base class like this one that extends AbstractSpringMarkLogicTest and defines
 * some basic configuration. Each test class for the project will then extend this base class to inherit all the
 * configuration and functionality.
 */
public abstract class AbstractSampleProjectTest extends AbstractSpringMarkLogicTest {

	/**
	 * A NamespaceProvider is used by Fragment instances for resolving prefixes in XPath expressions. By default, an
	 * instance of MarkLogicNamespaceProvider is used. This method can be overridden so that a project can provide its
	 * own implementation of NamespaceProvider (which typically will extend MarkLogicNamespaceProvider).
	 */
	@Override
	protected NamespaceProvider getNamespaceProvider() {
		return new SampleNamespaceProvider();
	}

	/**
	 * A common method to have in this abstract base class is one for quickly loading documents that can be used for
	 * testing. In this example, we create a simple person document in our sample namespace. We're accessing the
	 * MarkLogic DatabaseClient interface via the getClient() method, and then we use a simple method to write our XML
	 * document to the MarkLogic database.
	 *
	 * @param uri
	 * @param name
	 * @param description
	 */
	protected void loadPerson(String uri, String name, String description) {
		String xml = format(
			"<person xmlns='http://marklogic.com/sample'><name>%s</name><description>%s</description></person>",
			name, description);
		getDatabaseClient().newXMLDocumentManager().write(uri, new StringHandle(xml).withFormat(Format.XML));
	}
}
