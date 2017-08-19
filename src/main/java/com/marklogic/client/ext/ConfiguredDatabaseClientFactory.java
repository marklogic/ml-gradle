package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;

/**
 * Hides how a DatabaseClient is constructed based on the inputs in a DatabaseClientConfig object. The intent is that a
 * client can populate any set of properties on the DatabaseClientConfig, and an implementation of this interface will
 * determine how to construct a new DatabaseClient based on those properties.
 */
public interface ConfiguredDatabaseClientFactory {

	DatabaseClient newDatabaseClient(DatabaseClientConfig databaseClientConfig);

}
