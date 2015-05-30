package com.marklogic.appdeployer.mgmt.cpf;

import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.clientutil.LoggingObject;

public class CpfManager extends LoggingObject {

    private ManageClient client;

    public CpfManager(ManageClient client) {
        this.client = client;
    }
    
    public void createCpfConfig(String triggersDatabaseIdOrName, String json) {
        client.postJson("/manage/v2/databases/" + triggersDatabaseIdOrName + "/cpf-configs", json);
    }
}
