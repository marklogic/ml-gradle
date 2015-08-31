package com.rjrudin.marklogic.mgmt.flexrep;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

public class TargetManager extends AbstractResourceManager {

    private String databaseIdOrName;
    private String configIdOrName;

    public TargetManager(ManageClient client, String databaseIdOrName, String configIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
        this.configIdOrName = configIdOrName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/flexrep/configs/%s/targets", databaseIdOrName, configIdOrName);
    }

}
