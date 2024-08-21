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
