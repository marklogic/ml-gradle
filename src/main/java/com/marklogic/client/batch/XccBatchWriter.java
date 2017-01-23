package com.marklogic.client.batch;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.xcc.DefaultDocumentWriteOperationAdapter;
import com.marklogic.client.xcc.DocumentWriteOperationAdapter;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

import java.util.ArrayList;
import java.util.List;

/**
 * XCC implementation for batched writes. Most important thing here is we depend on an instance of
 * DocumentWriteOperationAdapter to adapt a DocumentWriteOperation instance into a Content instance.
 */
public class XccBatchWriter extends BatchWriterSupport {

	private List<ContentSource> contentSources;
	private int contentSourceIndex = 0;
	private DocumentWriteOperationAdapter documentWriteOperationAdapter;

	public XccBatchWriter(List<ContentSource> contentSources) {
		this.contentSources = contentSources;
		this.documentWriteOperationAdapter = new DefaultDocumentWriteOperationAdapter();
	}

	@Override
	public void write(final List<? extends DocumentWriteOperation> items) {
		if (contentSourceIndex >= contentSources.size()) {
			contentSourceIndex = 0;
		}

		final ContentSource contentSource = contentSources.get(contentSourceIndex);
		contentSourceIndex++;

		execute(new Runnable() {
			@Override
			public void run() {
				Session session = contentSource.newSession();
				int count = items.size();
				Content[] array = new Content[count];
				for (int i = 0; i < count; i++) {
					array[i] = documentWriteOperationAdapter.adapt(items.get(i));
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Writing " + count + " documents to MarkLogic");
				}
				try {
					session.insertContent(array);
					if (logger.isInfoEnabled()) {
						logger.info("Wrote " + count + " documents to MarkLogic");
					}
				} catch (RequestException e) {
					throw new RuntimeException("Unable to insert content: " + e.getMessage(), e);
				} finally {
					session.close();
				}
			}
		});
	}

	public void setDocumentWriteOperationAdapter(DocumentWriteOperationAdapter documentWriteOperationAdapter) {
		this.documentWriteOperationAdapter = documentWriteOperationAdapter;
	}
}
