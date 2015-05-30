package com.marklogic.appdeployer.mgmt.hosts;

import java.util.List;

import com.marklogic.appdeployer.mgmt.AbstractManager;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.util.Fragment;

public class HostManager extends AbstractManager {

    private ManageClient client;

    public HostManager(ManageClient client) {
        this.client = client;
    }

    public List<String> getHostIds() {
        return getHosts().getElementValues("/h:host-default-list/h:list-items/h:list-item/h:idref");
    }

    public List<String> getHostNames() {
        return getHosts().getElementValues("/h:host-default-list/h:list-items/h:list-item/h:nameref");
    }

    public Fragment getHosts() {
        return client.getXml("/manage/v2/hosts", "h", "http://marklogic.com/manage/hosts");
    }
}
