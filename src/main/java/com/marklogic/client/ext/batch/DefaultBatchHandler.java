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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.io.Format;

import java.util.List;

/**
 * Default implementation of BatchHandler that uses a DocumentManager to write documents, along with an optional
 * ServerTransform.
 */
public class DefaultBatchHandler extends LoggingObject implements BatchHandler {

	private Format contentFormat;
	private ServerTransform serverTransform;

	public DefaultBatchHandler() {
	}

	public DefaultBatchHandler(Format contentFormat, ServerTransform serverTransform) {
		this.contentFormat = contentFormat;
		this.serverTransform = serverTransform;
	}

	@Override
	public void handleBatch(DatabaseClient client, List<? extends DocumentWriteOperation> items) {
		DocumentManager<?, ?> mgr = buildDocumentManager(client);
		if (contentFormat != null) {
			mgr.setContentFormat(contentFormat);
		}

		DocumentWriteSet set = mgr.newWriteSet();
		set.addAll(items);

		int count = set.size();
		String connectionInfo = format("port: %d", client.getPort());
		if (client.getDatabase() != null) {
			connectionInfo += format("; database: %s", client.getDatabase());
		}

		if (logger.isInfoEnabled()) {
			logger.info("Writing " + count + " documents to MarkLogic; " + connectionInfo);
		}
		if (serverTransform != null) {
			mgr.write(set, serverTransform);
		} else {
			mgr.write(set);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Wrote " + count + " documents to MarkLogic; " + connectionInfo);
		}
	}

	protected DocumentManager<?, ?> buildDocumentManager(DatabaseClient client) {
		return client.newDocumentManager();
	}


	public void setServerTransform(ServerTransform serverTransform) {
		this.serverTransform = serverTransform;
	}

	public void setContentFormat(Format contentFormat) {
		this.contentFormat = contentFormat;
	}
}
