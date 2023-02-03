/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.xcc.DefaultDocumentWriteOperationAdapter;
import com.marklogic.client.ext.xcc.DocumentWriteOperationAdapter;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

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
		ContentSource contentSource = determineContentSourceToUse();
		Runnable runnable = buildRunnable(contentSource, items);
		executeRunnable(runnable, items);
	}

	protected ContentSource determineContentSourceToUse() {
		if (contentSourceIndex >= contentSources.size()) {
			contentSourceIndex = 0;
		}
		ContentSource contentSource = contentSources.get(contentSourceIndex);
		contentSourceIndex++;
		return contentSource;
	}

	protected Runnable buildRunnable(final ContentSource contentSource, final List<? extends DocumentWriteOperation> items) {
		return new Runnable() {
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
		};
	}

	public void setDocumentWriteOperationAdapter(DocumentWriteOperationAdapter documentWriteOperationAdapter) {
		this.documentWriteOperationAdapter = documentWriteOperationAdapter;
	}
}
