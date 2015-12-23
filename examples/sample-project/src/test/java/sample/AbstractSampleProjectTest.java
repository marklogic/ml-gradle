package sample;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.junit.spring.ModulesLoaderTestExecutionListener;
import com.marklogic.junit.spring.ModulesPath;

/**
 * A project that uses ml-junit will usually have a base class like this one that extends AbstractSpringTest and defines
 * some basic configuration. Each test class for the project will then extend this base class to inherit all the
 * configuration and functionality.
 */
/*
 * The Spring container is initialized via the custom SampleProjectTestConfig class. This classes tries to read
 * properties from the gradle.properties file at the root of the project. It provides two key beans - a
 * DatabaseClientProvider for obtaining a DatabaseClient that can talk to the MarkLogic REST API, and an XccTemplate,
 * which provides a simple interface for talking to an XDBC server. A DatabaseClientConfig captures the connection
 * properties - this can be used for initializing an alternate library to talk to the REST API, such as RestAssured.
 */
@ContextConfiguration(classes = { SampleProjectTestConfig.class })
/*
 * The ModulesLoaderTestExecutionListener handles loading new/modified modules when the test suite begins. It looks for
 * modules based on the paths defined by either ModulesPath or ModulesPaths (used for specifying multiple ModulesPath
 * instances). This supports a development cycle of code/test with no manual build step in between for loading
 * new/modified modules - the test framework will instead handle that for you.
 */
@TestExecutionListeners(value = { ModulesLoaderTestExecutionListener.class })
@ModulesPath(baseDir = "src/main/ml-modules")
public abstract class AbstractSampleProjectTest extends AbstractSpringTest {

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
        getClient().newXMLDocumentManager().write(uri, new StringHandle(xml).withFormat(Format.XML));
    }
}
