package com.rjrudin.marklogic.mgmt.cpf;

import com.rjrudin.marklogic.mgmt.ManageClient;

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
