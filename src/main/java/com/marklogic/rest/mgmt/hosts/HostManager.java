package com.marklogic.rest.mgmt.hosts;

import java.util.List;

import com.marklogic.rest.mgmt.AbstractManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

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
        return client.getXml("/manage/v2/hosts");
    }
}
