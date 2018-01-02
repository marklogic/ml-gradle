package com.marklogic.mgmt.resource.hosts;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * @return a map with an entry for each host, with the key of the host being the host ID and the value being the
	 * host name
	 */
	public Map<String, String> getHostIdsAndNames() {
    	Fragment xml = getHosts();
    	Map<String, String> map = new LinkedHashMap<>();
	    Namespace ns = Namespace.getNamespace("http://marklogic.com/manage/hosts");
    	for (Element el : xml.getElements("/h:host-default-list/h:list-items/h:list-item")) {
    		String hostId = el.getChildText("idref", ns);
    		String hostName = el.getChildText("nameref", ns);
    		map.put(hostId, hostName);
	    }
    	return map;
    }

    public Fragment getHosts() {
        return client.getXml("/manage/v2/hosts");
    }
}
