package com.marklogic.rest.mgmt.cpf;

import com.marklogic.rest.mgmt.ManageClient;

public class CpfConfigManager extends AbstractCpfResourceManager {

    public CpfConfigManager(ManageClient client) {
        super(client);
    }

    @Override
    protected String getIdFieldName() {
        return "domain-name";
    }

    @Override
    protected String getResourceName() {
        return "cpf-config";
    }
}
