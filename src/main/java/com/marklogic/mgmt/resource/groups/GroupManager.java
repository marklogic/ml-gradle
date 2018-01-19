package com.marklogic.mgmt.resource.groups;

import com.marklogic.mgmt.DeleteReceipt;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.LinkedHashMap;
import java.util.Map;

public class GroupManager extends AbstractResourceManager {

	public GroupManager(ManageClient manageClient) {
		super(manageClient);
	}

	@Override
	public DeleteReceipt delete(String payload, String... resourceUrlParams) {
		String resourceId = getResourceId(payload);
		if (resourceId != null && resourceId.toUpperCase().equals("DEFAULT")) {
			return new DeleteReceipt(resourceId, null, false);
		}
		return super.delete(payload);
	}

	public Map<String, String> getHostIdsAndNames(String groupName) {
		String path = super.getResourcePath(groupName);
		Fragment xml = getManageClient().getXml(path);

		Map<String, String> map = new LinkedHashMap<>();
		Namespace ns = Namespace.getNamespace("http://marklogic.com/manage/groups");
		for (Element el : xml.getElements("/g:group-default/g:relations/g:relation-group[g:typeref = 'hosts']/g:relation")) {
			String hostId = el.getChildText("idref", ns);
			String hostName = el.getChildText("nameref", ns);
			map.put(hostId, hostName);
		}

		return map;
	}
}
