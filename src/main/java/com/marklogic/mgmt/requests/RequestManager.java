package com.marklogic.mgmt.requests;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Element;

import java.util.List;

public class RequestManager extends AbstractManager {

	private ManageClient client;

	public RequestManager(ManageClient client) {
		this.client = client;
	}

	public Fragment getRequests() {
		return client.getXml("/manage/v2/requests");
	}

	public int getRequestCountForRelationId(String id) {
		return getRequestsForRelationId(id).size();
	}

	public List<Element> getRequestsForRelationId(String id) {
		return getRequests().getElements(format("//req:list-items/req:list-item[req:relation-id = '%s']", id));
	}
}
