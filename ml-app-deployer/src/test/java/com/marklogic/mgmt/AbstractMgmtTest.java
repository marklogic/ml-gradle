/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Base class for tests that just talk to the Mgmt API and don't depend on an AppDeployer instance.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class AbstractMgmtTest extends LoggingObject {

	@Autowired
	protected ManageConfig manageConfig;

	@Autowired
	protected AdminConfig adminConfig;

	// Intended to be used by subclasses
	protected ManageClient manageClient;
	protected AdminManager adminManager;

	@BeforeEach
	public void initializeManageClient() {
		manageClient = new ManageClient(manageConfig);
		adminManager = new AdminManager(adminConfig);
	}
}
