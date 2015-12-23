package com.rjrudin.marklogic.mgmt.security;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

public class ProtectedCollectionsManager extends AbstractResourceManager {

    public ProtectedCollectionsManager(ManageClient client) {
        super(client);
    }

    @Override
    protected boolean useAdminUser() {
        return true;
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
    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return getResourcesPath() + "/properties?collection=" + resourceNameOrId;
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return getResourcesPath() + "?collection=" + resourceNameOrId;
    }

}
