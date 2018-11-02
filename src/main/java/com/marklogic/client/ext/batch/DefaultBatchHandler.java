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
		for (DocumentWriteOperation item : items) {
			set.add(item);
		}

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
		if (logger.isInfoEnabled()) {
			logger.info("Wrote " + count + " documents to MarkLogic; " + connectionInfo);
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
