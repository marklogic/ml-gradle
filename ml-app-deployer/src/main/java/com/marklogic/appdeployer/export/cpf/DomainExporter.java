/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.export.cpf;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.cpf.DomainManager;

import java.io.File;

public class DomainExporter extends AbstractNamedResourceExporter {

	private String databaseIdOrName;

	public DomainExporter(ManageClient manageClient, String databaseIdOrName, String... domainNames) {
		super(manageClient, domainNames);
		this.databaseIdOrName = databaseIdOrName;
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new DomainManager(manageClient, databaseIdOrName);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getDomainsDir();
	}
}
