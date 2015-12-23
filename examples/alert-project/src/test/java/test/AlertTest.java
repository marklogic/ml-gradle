package test;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = { TestConfig.class })
public class AlertTest extends AbstractSpringTest {

    @Override
    public void deleteDocumentsBeforeTestRuns() {
        // We don't want to do this, as it will blow away our alert configuration
    }

    @Test
    public void test() {
        // URI of the document we'll insert
        final String uri = System.currentTimeMillis() + ".xml";

        // Insert a document into the collection that we have the alerting pipeline attached to
        XMLDocumentManager mgr = getClient().newXMLDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections("sample");
        mgr.write(uri, metadata, new StringHandle(
                "<test>This has the word hello in it, so it should trigger our alert action</test>")
                .withFormat(Format.XML));

        // Check ErrorLog.txt to verify that my-alert.xqy logged an entry
    }
}
