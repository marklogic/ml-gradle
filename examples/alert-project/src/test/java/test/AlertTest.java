package test;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit5.spring.AbstractSpringMarkLogicTest;
import org.junit.jupiter.api.Test;

public class AlertTest extends AbstractSpringMarkLogicTest {

	@Override
	public void deleteDocumentsBeforeTestRuns() {
		// Not doing this so that our alert config isn't deleted
	}

	@Test
	void test() {
		// URI of the document we'll insert
		final String uri = System.currentTimeMillis() + ".xml";

		// Insert a document into the collection that we have the alerting pipeline attached to
		XMLDocumentManager mgr = getDatabaseClient().newXMLDocumentManager();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.withCollections("sample");
		mgr.write(uri, metadata, new StringHandle(
			"<test>This has the word hello in it, so it should trigger our alert action</test>")
			.withFormat(Format.XML));

		// Check ErrorLog.txt to verify that my-alert.xqy logged an entry
	}
}
