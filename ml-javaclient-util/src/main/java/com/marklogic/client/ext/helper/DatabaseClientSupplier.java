/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.helper;

import com.marklogic.client.DatabaseClient;

import java.io.Closeable;
import java.util.function.Supplier;

/**
 * Preferred mechanism for lazy instantiation of a DatabaseClient. Will eventually deprecate DatabaseClientProvider in
 * favor of this.
 *
 * @since 6.2.0
 */
public class DatabaseClientSupplier implements Closeable, Supplier<DatabaseClient> {

	private DatabaseClient databaseClient;
	private final Supplier<DatabaseClient> databaseClientSupplier;

	/**
	 * @param databaseClientSupplier delegates construction of the client to the given supplier. Will then hold onto
	 *                               an instance of the client after it's created so that it's only created once.
	 */
	public DatabaseClientSupplier(Supplier<DatabaseClient> databaseClientSupplier) {
		this.databaseClientSupplier = databaseClientSupplier;
	}

	public DatabaseClient get() {
		if (databaseClient == null) {
			databaseClient = databaseClientSupplier.get();
		}
		return databaseClient;
	}

	@Override
	public void close() {
		if (databaseClient != null) {
			databaseClient.close();
		}
	}
}
