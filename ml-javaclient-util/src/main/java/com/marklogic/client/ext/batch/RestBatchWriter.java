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
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;

import java.util.Arrays;
import java.util.List;

/**
 * REST API-based implementation, using the Java Client API. By default, this will call release() on each of the
 * DatabaseClient objects that are passed in. Be sure to disable this if you want to keep using those DatabaseClient
 * objects.
 * <p>
 * To customize what this does with every batch, you can set a new instance of BatchHandler. This class defaults to using
 * DefaultBatchHandler; it'll pass its instances of Format and ServerTransform to that class.
 */
public class RestBatchWriter extends BatchWriterSupport {

	private List<DatabaseClient> databaseClients;
	private int clientIndex = 0;
	private boolean releaseDatabaseClients = true;

	private Format contentFormat;
	private ServerTransform serverTransform;
	private BatchHandler batchHandler;

	public RestBatchWriter(DatabaseClient databaseClient) {
		this(databaseClient, true);
	}

	public RestBatchWriter(DatabaseClient databaseClient, boolean releaseDatabaseClients) {
		this(Arrays.asList(databaseClient));
		this.releaseDatabaseClients = releaseDatabaseClients;
	}

	public RestBatchWriter(List<DatabaseClient> databaseClients) {
		this.databaseClients = databaseClients;
	}

	public RestBatchWriter(List<DatabaseClient> databaseClients, boolean releaseDatabaseClients, BatchHandler batchHandler) {
		this(databaseClients);
		this.releaseDatabaseClients = releaseDatabaseClients;
		this.batchHandler = batchHandler;
	}

	@Override
	public void write(List<? extends DocumentWriteOperation> items) {
		initialize();
		DatabaseClient client = determineDatabaseClientToUse();
		Runnable runnable = buildRunnable(client, items);
		executeRunnable(runnable, items);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (batchHandler == null) {
			DefaultBatchHandler dbh = new DefaultBatchHandler();
			dbh.setContentFormat(contentFormat);
			dbh.setServerTransform(serverTransform);
			this.batchHandler = dbh;
		}
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
				batchHandler.handleBatch(client, items);
			}
		};
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

	public void setContentFormat(Format contentFormat) {
		this.contentFormat = contentFormat;
	}

	public void setBatchHandler(BatchHandler batchHandler) {
		this.batchHandler = batchHandler;
	}
}
