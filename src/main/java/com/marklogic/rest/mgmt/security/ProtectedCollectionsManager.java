package com.marklogic.rest.mgmt.security;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class ProtectedCollectionsManager extends AbstractResourceManager {

    public ProtectedCollectionsManager(ManageClient client) {
        super(client);
    }

    @Override
    public String getResourcesPath() {
        return "/manage/v2/protected-collections";
    }

    @Override
    protected String getIdFieldName() {
        return "collection";
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId) {
        return getResourcesPath() + "/properties?collection=" + resourceNameOrId;
    }

    @Override
    public String getResourcePath(String resourceNameOrId) {
        return getResourcesPath() + "?collection=" + resourceNameOrId;
    }

}
