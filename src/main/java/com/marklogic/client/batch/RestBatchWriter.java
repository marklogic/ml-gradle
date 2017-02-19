package com.marklogic.client.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;

import java.util.Arrays;
import java.util.List;

/**
 * REST API-based implementation, using the Java Client API. By default, this will call release() on each of the
 * DatabaseClient objects that are passed in. Be sure to disable this if you want to keep using those DatabaseClient
 * objects.
 */
public class RestBatchWriter extends BatchWriterSupport {

	private List<DatabaseClient> databaseClients;
	private int clientIndex = 0;
	private boolean releaseDatabaseClients = true;
	private ServerTransform serverTransform;

	public RestBatchWriter(DatabaseClient databaseClient) {
		this(Arrays.asList(databaseClient));
	}

	public RestBatchWriter(List<DatabaseClient> databaseClients) {
		this.databaseClients = databaseClients;
	}

	@Override
	public void write(List<? extends DocumentWriteOperation> items) {
		DatabaseClient client = determineDatabaseClientToUse();
		Runnable runnable = buildRunnable(client, items);
		executeRunnable(runnable, items);
	}

	protected DatabaseClient determineDatabaseClientToUse() {
		if (clientIndex >= databaseClients.size()) {
			clientIndex = 0;
		}
		DatabaseClient client = databaseClients.get(clientIndex);
		clientIndex++;
		return client;
	}

	protected Runnable buildRunnable(final DatabaseClient client, final List<? extends DocumentWriteOperation> items) {
		return new Runnable() {
			@Override
			public void run() {
				DocumentManager<?, ?> mgr = buildDocumentManager(client);
				DocumentWriteSet set = mgr.newWriteSet();
				for (DocumentWriteOperation item : items) {
					set.add(item);
				}
				int count = set.size();
				if (logger.isDebugEnabled()) {
					logger.debug("Writing " + count + " documents to MarkLogic");
				}
				if (serverTransform != null) {
					mgr.write(set, serverTransform);
				} else {
					mgr.write(set);
				}
				if (logger.isInfoEnabled()) {
					logger.info("Wrote " + count + " documents to MarkLogic");
				}
			}
		};
	}

	/**
	 * Factored out so it can be overridden for e.g. those already using MarkLogic 9, who may need to set the content
	 * format on the manager.
	 *
	 * @param client
	 * @return
	 */
	protected DocumentManager<?, ?> buildDocumentManager(DatabaseClient client) {
		return client.newDocumentManager();
	}

	@Override
	public void waitForCompletion() {
		super.waitForCompletion();

		if (databaseClients != null && releaseDatabaseClients) {
			logger.info("Releasing DatabaseClient instances...");
			for (DatabaseClient client : databaseClients) {
				client.release();
			}
			logger.info("Finished releasing DatabaseClient instances");
		}
	}

	public void setReleaseDatabaseClients(boolean releaseDatabaseClients) {
		this.releaseDatabaseClients = releaseDatabaseClients;
	}

	public void setServerTransform(ServerTransform serverTransform) {
		this.serverTransform = serverTransform;
	}

	protected List<DatabaseClient> getDatabaseClients() {
		return databaseClients;
	}

	protected int getClientIndex() {
		return clientIndex;
	}

	protected boolean isReleaseDatabaseClients() {
		return releaseDatabaseClients;
	}

	protected ServerTransform getServerTransform() {
		return serverTransform;
	}
}
