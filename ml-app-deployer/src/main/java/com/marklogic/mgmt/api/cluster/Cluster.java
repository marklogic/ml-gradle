/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.cluster;

import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.ApiObject;
import com.marklogic.mgmt.resource.clusters.ClusterManager;

/**
 * Doesn't extend Resource yet because it doesn't conform well to the Resource interface.
 */
public class Cluster extends ApiObject {

    private API api;
    private AdminManager adminManager;

    public Cluster(API api, AdminManager adminManager) {
        this.api = api;
        this.adminManager = adminManager;
    }

    public void restart() {
        new ClusterManager(api.getManageClient()).restartLocalCluster(adminManager);
    }
}
