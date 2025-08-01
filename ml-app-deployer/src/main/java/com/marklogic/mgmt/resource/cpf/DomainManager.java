/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.cpf;

import com.marklogic.mgmt.ManageClient;

public class DomainManager extends AbstractCpfResourceManager {

    public DomainManager(ManageClient client, String databaseIdOrName) {
        super(client, databaseIdOrName);
    }
}
