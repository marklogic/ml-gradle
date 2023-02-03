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
package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.resource.hosts.HostManager;

import java.util.List;

public class AddHostNameTokensDeployerListener extends DeployerListenerSupport {

	@Override
	public void beforeCommandsExecuted(DeploymentContext context) {
		AppConfig appConfig = context.getAppConfig();
		if (appConfig.isAddHostNameTokens()) {
			HostManager hostManager = new HostManager(context.getCommandContext().getManageClient());
			List<String> hostNames = hostManager.getHostNames();
			int size = hostNames.size();
			for (int i = 1; i <= size; i++) {
				appConfig.getCustomTokens().put("mlHostName" + i, hostNames.get(i - 1));
			}
		}
	}

}
