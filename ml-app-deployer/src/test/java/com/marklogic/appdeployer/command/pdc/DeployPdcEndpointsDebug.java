/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.pdc;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.DefaultManageConfigFactory;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.util.SimplePropertySource;

import java.io.File;
import java.util.Properties;

// For manual testing of deploying integration endpoints to PDC.
public class DeployPdcEndpointsDebug {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("mlHost", System.getenv("ML_HOST"));
		props.setProperty("mlAuthentication", "cloud");
		props.setProperty("mlCloudApiKey", System.getenv("CLOUD_API_KEY"));
		props.setProperty("mlCloudBasePath", "/ml/ml12/default");
		props.setProperty("mlSimpleSsl", "true");

		ManageConfig manageConfig = new DefaultManageConfigFactory(
			new SimplePropertySource(props)
		).newManageConfig();
		final ManageClient manageClient = new ManageClient(manageConfig);


		DefaultAppConfigFactory factory = new DefaultAppConfigFactory(new SimplePropertySource(props));
		factory.setProjectDir(new File("ml-app-deployer/src/test/resources/cloud-project"));
		AppConfig appConfig = factory.newAppConfig();

		CommandContext context = new CommandContext(appConfig, manageClient, null);
		new DeployPdcEndpointsCommand().execute(context);
	}
}
