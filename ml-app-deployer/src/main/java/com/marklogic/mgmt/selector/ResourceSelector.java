/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.selector;

import com.marklogic.mgmt.ManageClient;

public interface ResourceSelector {

	ResourceSelection selectResources(ManageClient manageClient);

}
