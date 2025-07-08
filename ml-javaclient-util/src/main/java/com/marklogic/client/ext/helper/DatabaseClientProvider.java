/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.helper;

import com.marklogic.client.DatabaseClient;

public interface DatabaseClientProvider {

    public DatabaseClient getDatabaseClient();
}
