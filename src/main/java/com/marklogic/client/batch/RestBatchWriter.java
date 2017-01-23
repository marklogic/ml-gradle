package com.marklogic.client.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;

import java.util.List;

public class RestBatchWriter extends BatchWriterSupport {

	private List<DatabaseClient> databaseClients;
	private int clientIndex = 0;

	public RestBatchWriter(List<DatabaseClient> databaseClients) {
		this.databaseClients = databaseClients;
	}

	@Override
	public void write(final List<? extends DocumentWriteOperation> items) {
		if (clientIndex >= databaseClients.size()) {
			clientIndex = 0;
		}
		final DatabaseClient client = databaseClients.get(clientIndex);
		clientIndex++;

		execute(new Runnable() {
			@Override
			public void run() {
				GenericDocumentManager mgr = client.newDocumentManager();
				DocumentWriteSet set = mgr.newWriteSet();
				for (DocumentWriteOperation item : items) {
					set.add(item);
				}
				int count = set.size();
				if (logger.isDebugEnabled()) {
					logger.debug("Writing " + count + " documents to MarkLogic");
				}
				mgr.write(set);
				if (logger.isInfoEnabled()) {
					logger.info("Wrote " + count + " documents to MarkLogic");
				}
			}
		});
	}
}
