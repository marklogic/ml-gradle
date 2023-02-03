/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
