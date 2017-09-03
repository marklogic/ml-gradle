package com.marklogic.mgmt.resource.mimetypes;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class MimetypeManager extends AbstractResourceManager {

    public MimetypeManager(ManageClient client) {
        super(client);
    }

    @Override
    protected String getIdFieldName() {
        return "name";
    }

}
