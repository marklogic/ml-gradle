package sample;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.rjrudin.marklogic.junit.spring.AbstractSpringTest;
import com.rjrudin.marklogic.junit.spring.ModulesLoaderTestExecutionListener;
import com.rjrudin.marklogic.junit.spring.ModulesPath;

@ContextConfiguration(classes = { TestConfig.class })
@TestExecutionListeners(value = { ModulesLoaderTestExecutionListener.class })
@ModulesPath(baseDir = "src/main/ml-modules")
public class CpfTest extends AbstractSpringTest {

    @Test
    public void test() {
        // URI of the document we'll insert
        final String uri = "/cpf-example/test.xml";

        // Insert a document into the collection that we have a CPF pipeline attached to
        XMLDocumentManager mgr = getClient().newXMLDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections("cpf-test");
        mgr.write(uri, metadata, new StringHandle("<test/>").withFormat(Format.XML));

        // Wait for the task server to finish processing requests
        waitForTaskServerRequestsToFinish();

        // Verify that a property was set on our test document by the CPF action
        DocumentProperties props = mgr.readMetadata(uri, new DocumentMetadataHandle()).getProperties();
        assertEquals("The sample-prop property should have been set by the CPF action", "Hello from the CPF action",
                props.get(new QName("http://marklogic.com/sample", "sample-prop")));
    }

    /**
     * TODO Move this into ml-junit for reuse.
     */
    private void waitForTaskServerRequestsToFinish() {
        String xquery = "declare namespace ss = 'http://marklogic.com/xdmp/status/server'; ";
        xquery += "xdmp:server-status(xdmp:hosts(), xdmp:server('TaskServer'))/ss:request-statuses/ss:request-status";

        final int MAX_TRIES = 100;
        final int SLEEP_TIME = 500;

        logger.info("Waiting for task server requests to finish...");
        for (int i = 0; i < MAX_TRIES; i++) {
            String result = getXccTemplate().executeAdhocQuery(xquery);
            if (result == null || result.trim().length() == 0) {
                break;
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error from sleeping while waiting for task server to finish", e);
            }
        }
        logger.info("Task server no longer has any outstanding requests");
    }
}
