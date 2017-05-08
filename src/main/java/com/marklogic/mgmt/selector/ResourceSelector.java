package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.ManageClient;

public interface ResourceSelector {

	ResourceSelection selectResources(ManageClient manageClient);

}
