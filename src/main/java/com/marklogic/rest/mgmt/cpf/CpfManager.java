package com.marklogic.rest.mgmt.cpf;

import com.marklogic.clientutil.LoggingObject;
import com.marklogic.rest.mgmt.ManageClient;

public class CpfManager extends LoggingObject {

    private ManageClient client;

    public CpfManager(ManageClient client) {
        this.client = client;
    }
    
    public void createCpfConfig(String triggersDatabaseIdOrName, String json) {
        client.postJson("/manage/v2/databases/" + triggersDatabaseIdOrName + "/cpf-configs", json);
    }
}
