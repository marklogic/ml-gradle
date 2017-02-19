package com.marklogic.client.batch;

import com.marklogic.client.AbstractIntegrationTest;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.StringHandle;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RestBatchWriterTest extends AbstractIntegrationTest {

	@Test
	public void failureTest() {
		RestBatchWriter writer = new RestBatchWriter(newClient("Documents"));
		TestWriteListener testWriteListener = new TestWriteListener();
		writer.setWriteListener(testWriteListener);

		DocumentWriteOperation op = new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
			"/test.xml", null, new StringHandle("<hello>world</hello>asdf"));

		writer.initialize();
		writer.write(Arrays.asList(op));
		writer.waitForCompletion();

		Throwable caughtError = testWriteListener.caughtError;
		assertNotNull("An error should have been thrown due to the invalid XML", caughtError);
		assertTrue(caughtError instanceof FailedRequestException);
	}
}

class TestWriteListener implements WriteListener {
	public Throwable caughtError;

	@Override
	public void onWriteFailure(Throwable ex, List<? extends DocumentWriteOperation> items) {
		this.caughtError = ex;
	}
}