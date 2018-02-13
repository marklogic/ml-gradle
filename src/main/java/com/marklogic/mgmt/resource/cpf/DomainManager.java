package com.marklogic.mgmt.resource.cpf;

import com.marklogic.mgmt.ManageClient;

public class DomainManager extends AbstractCpfResourceManager {

    public DomainManager(ManageClient client, String databaseIdOrName) {
        super(client, databaseIdOrName);
    }
}
