/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api;

import com.marklogic.mgmt.DefaultManageConfigFactory;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.admin.DefaultAdminConfigFactory;
import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.SystemPropertySource;

public class APIUtil {

    public static API newAPIFromSystemProps() {
        PropertySource ps = new SystemPropertySource();
        ManageConfig mc = new DefaultManageConfigFactory(ps).newManageConfig();
        ManageClient client = new ManageClient(mc);

        AdminConfig ac = new DefaultAdminConfigFactory(ps).newAdminConfig();
        AdminManager adminManager = new AdminManager(ac);
        return new API(client, adminManager);
    }
}
