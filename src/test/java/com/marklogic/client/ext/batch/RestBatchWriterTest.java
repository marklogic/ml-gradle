package com.marklogic.client.ext.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestBatchWriterTest extends AbstractIntegrationTest {

	@Test
	public void failureTest() {
		RestBatchWriter writer = new RestBatchWriter(newClient("Documents"));

		DocumentWriteOperation op = new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
			"/test.xml", null, new StringHandle("<hello>world</hello>asdf"));

		writer.initialize();
		writer.write(Arrays.asList(op));

		try {
			writer.waitForCompletion();
			fail("The error caused by malformed XML should have been thrown");
		} catch (Exception ex) {
			// Expected
		}
	}

	@Test
	public void failureTestWithCustomListener() {
		RestBatchWriter writer = new RestBatchWriter(newClient("Documents"));
		TestWriteListener testWriteListener = new TestWriteListener();
		writer.setWriteListener(testWriteListener);

		DocumentWriteOperation op = new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
			"/test.xml", null, new StringHandle("<hello>world</hello>asdf"));

		writer.initialize();
		writer.write(Arrays.asList(op));
		writer.waitForCompletion();

		Throwable caughtError = testWriteListener.caughtError;
		assertNotNull(caughtError, "An error should have been thrown due to the invalid XML");
		assertTrue(caughtError instanceof FailedRequestException);
	}

	@Test
	public void writeDocumentWithTransform() throws IOException {
		DatabaseClient client = newClient("Documents");
		Resource transform = new FileSystemResource(Paths.get("src", "test", "resources", "transform", "simple.xqy").toString());
		TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();
		FileHandle fileHandle = new FileHandle(transform.getFile());
		fileHandle.setFormat(Format.XML);
		transMgr.writeXQueryTransform("simple", fileHandle);


		DocumentWriteOperation op = new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
			"/test.xml", null, new StringHandle("<hello>world</hello>"));

		RestBatchWriter writer = new RestBatchWriter(client);
		writer.setServerTransform(new ServerTransform("simple"));
		writer.setContentFormat(Format.XML);
		writer.initialize();
		writer.write(Arrays.asList(op));
		writer.waitForCompletion();

		client = newClient("Documents");
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPage page = docMgr.read("/test.xml");
		StringHandle handle = page.nextContent(new StringHandle());
		assertTrue(handle.toString().contains("<transform/>"));

	}
}

class TestWriteListener extends WriteListenerSupport {
	public Throwable caughtError;

	@Override
	public void onWriteFailure(Throwable ex, List<? extends DocumentWriteOperation> items) {
		this.caughtError = ex;
	}
}
