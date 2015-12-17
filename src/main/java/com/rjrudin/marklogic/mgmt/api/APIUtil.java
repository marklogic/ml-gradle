package com.rjrudin.marklogic.mgmt.api;

import com.rjrudin.marklogic.mgmt.DefaultManageConfigFactory;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.ManageConfig;
import com.rjrudin.marklogic.mgmt.admin.AdminConfig;
import com.rjrudin.marklogic.mgmt.admin.AdminManager;
import com.rjrudin.marklogic.mgmt.admin.DefaultAdminConfigFactory;
import com.rjrudin.marklogic.mgmt.util.PropertySource;
import com.rjrudin.marklogic.mgmt.util.SystemPropertySource;

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
