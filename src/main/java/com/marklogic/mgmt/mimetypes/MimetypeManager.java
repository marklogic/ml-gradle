package com.marklogic.mgmt.mimetypes;

import com.marklogic.mgmt.AbstractResourceManager;
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
